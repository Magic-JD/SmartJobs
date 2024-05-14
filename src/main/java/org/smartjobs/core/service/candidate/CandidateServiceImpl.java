package org.smartjobs.core.service.candidate;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.exception.categories.ApplicationExceptions.HashKnownButCvNotFound;
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
    public List<ProcessedCv> getFullCandidateInfo(String username, long roleId) {
        return cvDao.getAllSelected(username, roleId);
    }

    @Override
    @Cacheable(value = "cv-name", key = "{#username, #roleId}")
    public List<CandidateData> getCurrentCandidates(String username, long roleId) {
        return cvDao.getAllNames(username, roleId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#username, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#username, #roleId}")
    })
    public void updateCandidateCvs(String username, long roleId, List<Optional<FileInformation>> fileInformationList) {
        var counter = new AtomicInteger(0);
        var total = fileInformationList.size();

        var processedCvs = ConcurrencyUtil.virtualThreadList(fileInformationList, fileInformation -> {
            var processedCv = fileInformation.flatMap(this::processCv);
            sseService.send(username, "progress-upload", STR. "<div>Uploaded: \{ counter.incrementAndGet() }/\{ total }</div>" );
            return processedCv;
        });
        List<ProcessedCv> list = processedCvs.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        if (processedCvs.isEmpty()) {
            return;
        }
        cvDao.addCvsToRepository(username, roleId, list);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#username, #currentRole}"),
            @CacheEvict(value = "cv-name", key = "{#username, #currentRole}")
    })
    public void deleteCandidate(String username, long currentRole, long cvId) {
        cvDao.deleteByCvId(cvId);
    }

    private Optional<ProcessedCv> processCv(FileInformation fileInformation) {
        String hash = fileInformation.fileHash();
        if (cvDao.knownHash(hash)) {
            Optional<ProcessedCv> byHash = cvDao.getByHash(hash)
                    .map(pc -> new ProcessedCv(null, pc.name(), true, pc.fileHash(), pc.condensedDescription()));
            if (byHash.isEmpty()) {
                throw new HashKnownButCvNotFound(hash);
            }
            return byHash;
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
            @CacheEvict(value = "cv-currently-selected", key = "{#currentUsername, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#currentUsername, #roleId}")
    })
    public Optional<CandidateData> toggleCandidateSelect(String currentUsername, long roleId, long cvId, boolean select) {
        return cvDao.updateCurrentlySelectedById(cvId, select);
    }

    @Override
    @Cacheable(value = "cv-currently-selected", key = "{#username, #currentRole}")
    public int findSelectedCandidateCount(String username, long currentRole) {
        return cvDao.findSelectedCandidateCount(username, currentRole);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cv-currently-selected", key = "{#username, #roleId}"),
            @CacheEvict(value = "cv-name", key = "{#username, #roleId}")
    })
    public void deleteAllCandidates(String username, long roleId) {
        cvDao.deleteAllCandidates(username, roleId);
    }
}
