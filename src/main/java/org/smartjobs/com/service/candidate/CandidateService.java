package org.smartjobs.com.service.candidate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.dal.CvDao;
import org.smartjobs.com.service.candidate.data.*;
import org.smartjobs.com.service.file.data.FileInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final GptClient client;
    private final CvDao cvDao;
    private final int maxTop;

    @Autowired
    public CandidateService(GptClient client, CvDao cvDao, @Value("${candidate.max.top}") int maxTop) {
        this.client = client;
        this.cvDao = cvDao;
        this.maxTop = maxTop;
    }

    public List<CandidateData> getCurrentCandidates() {
        return cvDao.getAllNames();
    }

    public void updateCandidateCvs(Stream<FileInformation> fileInformationList) {
        fileInformationList.forEach(fileInformation -> {
            var nameFuture = supplyAsync(() -> client.extractCandidateName(fileInformation.fileContent()));
            var descriptionFuture = supplyAsync(() -> client.anonymousCandidateDescription(fileInformation.fileContent()));
            String name = nameFuture.join();
            String cvDescription = descriptionFuture.join();
            cvDao.addCvToRepository(new ProcessedCv(name, fileInformation.filePath(), cvDescription, fileInformation.fileContent()));
        });
    }

    public Optional<ListingAnalysis> analyseListing(String listing) {
        var allCvs = cvDao.getAll();
        if (allCvs.isEmpty()) {
            logger.info("There are no candidates to assess.");
            return empty();
        }
        List<JobMatch> bestMatches = allCvs.parallelStream()
                .map(cv -> jobMatch(listing, cv))
                .sorted(Comparator.comparing(JobMatch::match, Comparator.reverseOrder()))
                .limit(maxTop).toList();
        JobMatch bestMatch = bestMatches.getFirst();
        String gptJustification = client.justifyDecision(bestMatch.match(), bestMatch.fullText(), listing);
        List<TopMatch> topMatches = bestMatches.stream().map(jm -> new TopMatch(jm.name(), jm.match(), jm.cvDownload())).toList();
        String topScorerName = topMatches.getFirst().name();
        return of(new ListingAnalysis(topMatches, gptJustification, topScorerName, bestMatch.cvDownload(), topMatches.size()));
    }

    private JobMatch jobMatch(String listingDescription, ProcessedCv cv) {
        int matchPercentage = client.determineMatchPercentage(listingDescription, cv.condensedDescription());
        logger.debug("CV ID:{} NAME:{} given a match percentage of {}%", cv.fileLocation(), cv.name(), matchPercentage);
        return new JobMatch(cv.name(), matchPercentage, cv.fullDescription(), cv.fileLocation());
    }

    public void deleteCandidate(String filePath) {
        cvDao.deleteByFilePath(filePath);
    }
}
