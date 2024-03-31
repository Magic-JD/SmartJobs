package org.smartjobs.com.service.candidate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.cache.CandidateCache;
import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.dal.CvDao;
import org.smartjobs.com.service.candidate.data.CandidateData;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.file.data.FileInformation;
import org.smartjobs.com.utils.ConcurrencyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final GptClient client;
    private final CvDao cvDao;

    private final CandidateCache cache;

    @Autowired
    public CandidateService(GptClient client, CvDao cvDao, CandidateCache candidateCache) {
        this.client = client;
        this.cvDao = cvDao;
        this.cache = candidateCache;
    }

    public List<ProcessedCv> getFullCandidateInfo(String userName) {
        return cvDao.getAllSelected();
    }

    public List<CandidateData> getCurrentCandidates(String userName) {
        return cache.getFromCacheOrCompute(userName, _ -> cvDao.getAllNames());
    }

    public void updateCandidateCvs(String username, List<Optional<FileInformation>> fileInformationList) {
        cache.clearCache(username);
        var processedCvs = ConcurrencyUtil.virtualThreadList(fileInformationList, fileInformation -> fileInformation.flatMap(this::processCv));
        List<ProcessedCv> list = processedCvs.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        if (processedCvs.isEmpty()) {
            return;
        }
        cvDao.addCvsToRepository(list);
        cache.clearCache(username);
    }

    public void deleteCandidate(String username, long cvId) {
        cache.clearCache(username);
        cvDao.deleteByCvId(cvId);
    }

    private Optional<ProcessedCv> processCv(FileInformation fileInformation) {
        if (cvDao.knownHash(fileInformation.fileHash())) {
            return Optional.empty();
        }
        var nameFuture = supplyAsync(() -> client.extractCandidateName(fileInformation.fileContent()));
        var descriptionFuture = supplyAsync(() -> client.anonymousCandidateDescription(fileInformation.fileContent()));
        var name = nameFuture.join();
        var cvDescription = descriptionFuture.join();
        if (name.isEmpty() || cvDescription.isEmpty()) {
            logger.error("Either CV name {} or description {} is empty.", name.orElse("???"), cvDescription.orElse("???"));
            return Optional.empty();
        } else {
            return Optional.of(new ProcessedCv(null, name.get(), true, fileInformation.fileHash(), cvDescription.get()));
        }
    }

    public Optional<CandidateData> toggleCandidateSelect(String currentUsername, long cvId, boolean select) {
        cache.clearCache(currentUsername);
        return cvDao.updateCurrentlySelectedById(cvId, select);
    }
}
