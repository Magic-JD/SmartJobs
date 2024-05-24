package org.smartjobs.core.service.candidate;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.CvData;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.failures.ProcessFailure;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.ports.dal.CvDal;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.EventService;
import org.smartjobs.core.service.event.events.ErrorEvent;
import org.smartjobs.core.service.event.events.ProgressEvent;
import org.smartjobs.core.utils.ConcurrencyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.smartjobs.core.failures.ProcessFailure.EXISTING_CANDIDATE;
import static org.smartjobs.core.failures.ProcessFailure.LLM_FAILURE_UPLOADING;

@Service
@Slf4j
public class CandidateServiceImpl implements CandidateService {

    private final AiService aiService;
    private final CvDal cvDal;
    private final EventService eventService;
    private final CreditService creditService;


    @Autowired
    public CandidateServiceImpl(AiService aiService, CvDal cvDal, EventService eventService, CreditService creditService) {
        this.aiService = aiService;
        this.cvDal = cvDal;
        this.eventService = eventService;
        this.creditService = creditService;
    }

    @Override
    public List<ProcessedCv> getFullCandidateInfo(long userId, long roleId) {
        return cvDal.getAllSelected(userId, roleId);
    }

    @Override
    @Cacheable(value = "cv-name", key = "{#userId, #roleId}")
    public List<CandidateData> getCurrentCandidates(long userId, long roleId) {
        return cvDal.getAllNames(userId, roleId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#userId, #roleId}")
    })
    public List<ProcessedCv> updateCandidateCvs(long userId, long roleId, List<Either<ProcessFailure, FileInformation>> fileInformationList) {
        var counter = new AtomicInteger(0);
        var total = fileInformationList.size();

        var processedCvs = ConcurrencyUtil.virtualThreadListMap(
                fileInformationList,
                fileInformation -> processAndUpdateProgress(userId, roleId, fileInformation, counter, total)
        );

        List<ProcessedCv> successfullyProcessed = processedCvs.stream()
                .filter(Either::isRight)
                .map(Either::get)
                .toList();
        List<ProcessFailure> processFailures = processedCvs.stream().filter(Either::isLeft).map(Either::getLeft).toList();
        if (!processFailures.isEmpty()) {
            creditService.refund(userId, processFailures.size());
            eventService.sendEvent(new ErrorEvent(userId, processFailures));
        }
        cvDal.addCvsToRepository(userId, roleId, successfullyProcessed);
        return successfullyProcessed;
    }

    private Either<ProcessFailure, ProcessedCv> processAndUpdateProgress(long userId, long roleId, Either<ProcessFailure, FileInformation> fileInformation, AtomicInteger counter, int total) {
        var processedCv = fileInformation.flatMap(fi -> this.processCv(fi, userId, roleId));
        eventService.sendEvent(new ProgressEvent(userId, counter.incrementAndGet(), total));
        return processedCv;
    }

    private Either<ProcessFailure, ProcessedCv> processCv(FileInformation fileInformation, long userId, long roleId) {
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
        List<CandidateData> candidateData = cvDal.getByDataId(coreData.id());
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
            @CacheEvict(value = "cv-name", key = "{#userId, #roleId}")
    })
    public void deleteCandidate(long userId, long roleId, long candidateId) {
        cvDal.deleteByCandidateId(candidateId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#userId, #roleId}")
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
            @CacheEvict(value = "cv-name", key = "{#userId, #roleId}")
    })
    public void deleteAllCandidates(long userId, long roleId) {
        cvDal.deleteAllCandidates(userId, roleId);
    }
}
