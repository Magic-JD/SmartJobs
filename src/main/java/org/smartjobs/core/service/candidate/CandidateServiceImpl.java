package org.smartjobs.core.service.candidate;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.CvData;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.ErrorEvent;
import org.smartjobs.core.event.events.ProgressEvent;
import org.smartjobs.core.failures.ProcessFailure;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.ports.dal.CvDal;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.utils.ConcurrencyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.smartjobs.core.failures.ProcessFailure.*;

@Service
@Slf4j
public class CandidateServiceImpl implements CandidateService {

    private final AiService aiService;
    private final CvDal cvDal;
    private final EventEmitter eventEmitter;
    private final CreditService creditService;
    private final FileHandler fileHandler;


    @Autowired
    public CandidateServiceImpl(AiService aiService, CvDal cvDal, EventEmitter eventEmitter, CreditService creditService, FileHandler fileHandler) {
        this.aiService = aiService;
        this.cvDal = cvDal;
        this.eventEmitter = eventEmitter;
        this.creditService = creditService;
        this.fileHandler = fileHandler;
    }

    @Override
    public List<ProcessedCv> getFullCandidateInfo(long userId, long roleId) {
        return cvDal.getAllSelected(userId, roleId);
    }

    @Override
    @Cacheable(value = "cv-criteriaDescription", key = "{#userId, #roleId}")
    public List<CandidateData> getCurrentCandidates(long userId, long roleId) {
        return cvDal.getAllNames(userId, roleId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-criteriaDescription", key = "{#userId, #roleId}")
    })
    public List<ProcessedCv> updateCandidateCvs(long userId, long roleId, List<MultipartFile> files) {
        var handledFiles = files.stream()
                .map(fileHandler::handleFile)
                .distinct()
                .toList();
        var fileInformation = handledFiles.stream().map(hfo -> hfo.map(Either::<ProcessFailure, FileInformation>right).orElse(Either.left(FAILURE_TO_READ_FILE))).toList();
        var counter = new AtomicInteger(0);
        var total = files.size();

        var processedCvs = ConcurrencyUtil.virtualThreadListMap(
                fileInformation,
                fi -> processAndUpdateProgress(userId, roleId, fi, counter, total)
        );

        List<ProcessedCv> successfullyProcessed = processedCvs.stream()
                .filter(Either::isRight)
                .map(Either::get)
                .toList();
        List<ProcessFailure> processFailures = processedCvs.stream().filter(Either::isLeft).map(Either::getLeft).toList();
        if (!processFailures.isEmpty()) {
            creditService.refund(userId, processFailures.size());
            eventEmitter.sendEvent(new ErrorEvent(userId, processFailures));
        }
        cvDal.addCvsToRepository(userId, roleId, successfullyProcessed);
        return successfullyProcessed;
    }

    private Either<ProcessFailure, ProcessedCv> processAndUpdateProgress(long userId, long roleId, Either<ProcessFailure, FileInformation> fileInformation, AtomicInteger counter, int total) {
        var processedCv = fileInformation.flatMap(fi -> this.extractInformation(fi, userId, roleId));
        eventEmitter.sendEvent(new ProgressEvent(userId, counter.incrementAndGet(), total));
        return processedCv;
    }

    private Either<ProcessFailure, ProcessedCv> extractInformation(FileInformation fileInformation, long userId, long roleId) {
        String hash = fileInformation.fileHash();
        var existingData = cvDal.getByHash(hash);
        if (existingData.isPresent()) {
            return preexistingData(fileInformation, userId, roleId, existingData.get());
        }
        var nameFuture = supplyAsync(() -> aiService.extractCandidateName(fileInformation.fileContent()));
        var descriptionFuture = supplyAsync(() -> aiService.anonymizeCv(fileInformation.fileContent()));
        var name = nameFuture.join();
        var cvDescription = descriptionFuture.join();
        var processedCv = name
                .flatMap(n -> cvDescription.map(cvd -> Either.<ProcessFailure, ProcessedCv>right(new ProcessedCv(null, n, true, hash, cvd))))
                .orElse(Either.left(LLM_FAILURE_UPLOADING));
        if (processedCv.isLeft() && log.isErrorEnabled()) {
            log.error("Either CV position {} or description {} is empty.", name.orElse("???"), cvDescription.orElse("???"));
        }
        return processedCv;
    }

    private Either<ProcessFailure, ProcessedCv> preexistingData(FileInformation fileInformation, long userId, long roleId, CvData coreData) {
        List<CandidateData> candidateData = cvDal.getByCvId(coreData.id());
        if (candidateData.isEmpty()) {
            return aiService
                    .extractCandidateName(fileInformation.fileContent())
                    .map(name -> Either.<ProcessFailure, ProcessedCv>right(new ProcessedCv(coreData.id(), name, true, coreData.fileHash(), coreData.condensedDescription())))
                    .orElse(Either.left(LLM_FAILURE_UPLOADING));
        } else {
            String name = candidateData.getFirst().name();
            var existingRowForUser = candidateData.stream().filter(cd -> cd.userId() == userId && cd.roleId() == roleId).findFirst();
            if (existingRowForUser.isPresent()) {
                CandidateData currentData = existingRowForUser.get();
                if (!currentData.currentlySelected()) {
                    cvDal.updateCurrentlySelectedById(currentData.id(), true);
                }
                return Either.left(EXISTING_CANDIDATE);
            }
            return Either.right(new ProcessedCv(coreData.id(), name, true, coreData.fileHash(), coreData.condensedDescription()));
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-criteriaDescription", key = "{#userId, #roleId}")
    })
    public void deleteCandidate(long userId, long roleId, long candidateId) {
        cvDal.deleteByCandidateId(candidateId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-criteriaDescription", key = "{#userId, #roleId}")
    })
    public Optional<CandidateData> toggleCandidateSelect(long userId, long roleId, long cvId, boolean select) {
        return cvDal.updateCurrentlySelectedById(cvId, select);
    }

    @Override
    @Cacheable(value = "cv-currently-selected", key = "{#userId, #roleId}")
    public int findSelectedCandidateCount(long userId, long roleId) {
        return cvDal.findSelectedCandidateCount(userId, roleId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-criteriaDescription", key = "{#userId, #roleId}")
    })
    public void deleteAllCandidates(long userId, long roleId) {
        cvDal.deleteAllCandidates(userId, roleId);
    }
}
