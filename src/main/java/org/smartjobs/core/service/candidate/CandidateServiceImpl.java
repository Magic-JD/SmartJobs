package org.smartjobs.core.service.candidate;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.ports.dal.CvDao;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.utils.ConcurrencyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
@Slf4j
public class CandidateServiceImpl implements CandidateService {

    private final AiService client;
    private final CvDao cvDao;

    @Autowired
    public CandidateServiceImpl(AiService client, CvDao cvDao) {
        this.client = client;
        this.cvDao = cvDao;
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
        var processedCvs = ConcurrencyUtil.virtualThreadList(fileInformationList, fileInformation -> fileInformation.flatMap(this::processCv));
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
    @CacheEvict(value = "cv-name", key = "{#username, #currentRole}")
    public void deleteCandidate(String username, long currentRole, long cvId) {
        cvDao.deleteByCvId(cvId);
    }

    private Optional<ProcessedCv> processCv(FileInformation fileInformation) {
        if (cvDao.knownHash(fileInformation.fileHash())) {
            return Optional.empty();
        }
        var nameFuture = supplyAsync(() -> client.extractCandidateName(fileInformation.fileContent()));
        var descriptionFuture = supplyAsync(() -> client.anonymizeCv(fileInformation.fileContent()));
        var name = nameFuture.join();
        var cvDescription = descriptionFuture.join();
        if (name.isEmpty() || cvDescription.isEmpty()) {
            if (log.isErrorEnabled()) {
                log.error("Either CV position {} or description {} is empty.", name.orElse("???"), cvDescription.orElse("???"));
            }
            return Optional.empty();
        } else {
            return Optional.of(new ProcessedCv(null, name.get(), true, fileInformation.fileHash(), cvDescription.get()));
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
