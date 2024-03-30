package org.smartjobs.com.service.candidate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.cache.CandidateCache;
import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.dal.CvDao;
import org.smartjobs.com.service.candidate.data.CandidateData;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.file.data.FileInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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
        return cvDao.getAll();
    }

    public List<CandidateData> getCurrentCandidates(String userName) {
        return cache.getFromCacheOrCompute(userName, _ -> cvDao.getAllNames());
    }

    public void updateCandidateCvs(String username, Stream<FileInformation> fileInformationList) {
        cache.clearCache(username);
        fileInformationList.forEach(fileInformation -> {
            var nameFuture = supplyAsync(() -> client.extractCandidateName(fileInformation.fileContent()));
            var descriptionFuture = supplyAsync(() -> client.anonymousCandidateDescription(fileInformation.fileContent()));
            var name = nameFuture.join();
            var cvDescription = descriptionFuture.join();
            if (name.isEmpty() || cvDescription.isEmpty()) {
                logger.error("Either CV name {} or description {} is empty.", name.orElse("???"), cvDescription.orElse("???"));
            } else {
                cvDao.addCvToRepository(new ProcessedCv(null, name.get(), cvDescription.get()));
            }

        });
    }

    public void deleteCandidate(String username, long cvId) {
        cache.clearCache(username);
        cvDao.deleteByCvId(cvId);
    }
}
