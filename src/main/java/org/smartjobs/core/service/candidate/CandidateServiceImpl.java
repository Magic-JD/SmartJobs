package org.smartjobs.core.service.candidate;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.CvData;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.ports.dal.CvDao;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.SseService;
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

@Service
@Slf4j
public class CandidateServiceImpl implements CandidateService {

    private final AiService aiService;
    private final CvDao cvDao;
    private final SseService sseService;


    @Autowired
    public CandidateServiceImpl(AiService aiService, CvDao cvDao, SseService sseService) {
        this.aiService = aiService;
        this.cvDao = cvDao;
        this.sseService = sseService;
    }

    @Override
    public List<ProcessedCv> getFullCandidateInfo(long userId, long roleId) {
        return cvDao.getAllSelected(userId, roleId);
    }

    @Override
    @Cacheable(value = "cv-name", key = "{#userId, #roleId}")
    public List<CandidateData> getCurrentCandidates(long userId, long roleId) {
        return cvDao.getAllNames(userId, roleId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#userId, #roleId}")
    })
    public void updateCandidateCvs(long userId, long roleId, List<Optional<FileInformation>> fileInformationList) {
        var counter = new AtomicInteger(0);
        var total = fileInformationList.size();

        var processedCvs = ConcurrencyUtil.virtualThreadList(
                fileInformationList,
                fileInformation -> processAndUpdateProgress(userId, roleId, fileInformation, counter, total)
        );

        List<ProcessedCv> list = processedCvs.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        cvDao.addCvsToRepository(userId, roleId, list);
    }

    private Optional<ProcessedCv> processAndUpdateProgress(long userId, long roleId, Optional<FileInformation> fileInformation, AtomicInteger counter, int total) {
        var processedCv = fileInformation.flatMap(fi -> this.processCv(fi, userId, roleId));
        sseService.send(userId, "progress-upload", STR. "<div>Uploaded: \{ counter.incrementAndGet() }/\{ total }</div>" );
        return processedCv;
    }

    private Optional<ProcessedCv> processCv(FileInformation fileInformation, long userId, long roleId) {
        String hash = fileInformation.fileHash();
        Optional<CvData> coreData = cvDao.getByHash(hash);
        if (coreData.isPresent()) {
            CvData cvData = coreData.get();
            List<CandidateData> candidateData = cvDao.getByDataId(cvData.id());
            if (candidateData.isEmpty()) {
                var nameFuture = supplyAsync(() -> aiService.extractCandidateName(fileInformation.fileContent()));
                return nameFuture.join().map(name -> new ProcessedCv(null, name, true, cvData.fileHash(), cvData.condensedDescription()));
            } else {
                String name = candidateData.getFirst().name();
                var existingRowForUser = candidateData.stream().filter(cd -> cd.userId() == userId && cd.roleId() == roleId).findFirst();
                if (existingRowForUser.isPresent()) {
                    CandidateData currentData = existingRowForUser.get();
                    if (!currentData.currentlySelected()) {
                        cvDao.updateCurrentlySelectedById(currentData.id(), true);
                    }
                    return Optional.empty();
                }
                return Optional.of(new ProcessedCv(cvData.id(), name, true, cvData.fileHash(), cvData.condensedDescription()));
            }
        }
        var nameFuture = supplyAsync(() -> aiService.extractCandidateName(fileInformation.fileContent()));
        var descriptionFuture = supplyAsync(() -> aiService.anonymizeCv(fileInformation.fileContent()));
        var name = nameFuture.join();
        var cvDescription = descriptionFuture.join();
        if (name.isEmpty() || cvDescription.isEmpty()) {
            if (log.isErrorEnabled()) {
                log.error("Either CV position {} or description {} is empty.", name.orElse("???"), cvDescription.orElse("???"));
            }
            return Optional.empty();
        } else {
            return Optional.of(new ProcessedCv(null, name.get(), true, hash, cvDescription.get()));
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#userId, #roleId}")
    })
    public void deleteCandidate(long userId, long roleId, long candidateId) {
        cvDao.deleteByCandidateId(candidateId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#userId, #roleId}")
    })
    public Optional<CandidateData> toggleCandidateSelect(long userId, long roleId, long cvId, boolean select) {
        return cvDao.updateCurrentlySelectedById(cvId, select);
    }

    @Override
    @Cacheable(value = "cv-currently-selected", key = "{#userId, #roleId}")
    public int findSelectedCandidateCount(long userId, long roleId) {
        return cvDao.findSelectedCandidateCount(userId, roleId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#userId, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#userId, #roleId}")
    })
    public void deleteAllCandidates(long userId, long roleId) {
        cvDao.deleteAllCandidates(userId, roleId);
    }
}
