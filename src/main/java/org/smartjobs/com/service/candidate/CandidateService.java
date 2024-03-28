package org.smartjobs.com.service.candidate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.dal.CvDao;
import org.smartjobs.com.service.candidate.data.CandidateData;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.file.data.FileInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final GptClient client;
    private final CvDao cvDao;

    private static final Map<String, List<CandidateData>> candidates = new ConcurrentHashMap<>();

    @Autowired
    public CandidateService(GptClient client, CvDao cvDao) {
        this.client = client;
        this.cvDao = cvDao;
    }

    public List<ProcessedCv> getFullCandidateInfo(String userName) {
        return cvDao.getAll();
    }

    public List<CandidateData> getCurrentCandidates(String userName) {
        return candidates.computeIfAbsent(userName, _ -> cvDao.getAllNames());
    }

    public void updateCandidateCvs(String username, Stream<FileInformation> fileInformationList) {
        candidates.remove(username);
        fileInformationList.forEach(fileInformation -> {
            var nameFuture = supplyAsync(() -> client.extractCandidateName(fileInformation.fileContent()));
            var descriptionFuture = supplyAsync(() -> client.anonymousCandidateDescription(fileInformation.fileContent()));
            var name = nameFuture.join();
            var cvDescription = descriptionFuture.join();
            if (name.isEmpty() || cvDescription.isEmpty()) {
                logger.error("Either CV name {} or description {} is empty.", name.orElse("???"), cvDescription.orElse("???"));
            } else {
                cvDao.addCvToRepository(new ProcessedCv(name.get(), "FILE LOCATION NO LONGER STORED", cvDescription.get(), fileInformation.fileContent()));
            }

        });
    }

    public void deleteCandidate(String username, String filePath) {
        candidates.remove(username);
        cvDao.deleteByFilePath(filePath);
    }
}
