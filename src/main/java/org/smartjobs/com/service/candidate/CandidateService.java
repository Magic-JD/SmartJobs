package org.smartjobs.com.service.candidate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.client.gpt.response.GptUserExtraction;
import org.smartjobs.com.repository.CvDAO;
import org.smartjobs.com.repository.data.Cv;
import org.smartjobs.com.service.candidate.data.JobMatch;
import org.smartjobs.com.service.file.data.FileInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final GptClient client;
    private final CvDAO repository;

    @Autowired
    public CandidateService(GptClient client, CvDAO repository) {
        this.client = client;
        this.repository = repository;
    }

    public List<String> getCurrentCandidates() {
        return repository.findAll().stream().map(Cv::getCandidateName).toList();
    }

    public void updateCandidateCv(FileInformation fileInformation) {
        var nameFuture = supplyAsync(() -> client.extractCandidateName(fileInformation.fileContent()));
        var descriptionFuture = supplyAsync(() -> client.anonymousCandidateDescription(fileInformation.fileContent()));
        String name = nameFuture.join();
        String cvDescription = descriptionFuture.join();
        GptUserExtraction gptUserExtraction = new GptUserExtraction(name, cvDescription);
        Cv cv = Cv.builder()
                .candidateName(gptUserExtraction.name())
                .fullText(fileInformation.fileContent())
                .condensedText(gptUserExtraction.description())
                .filePath(fileInformation.filePath())
                .build();
        logger.debug("Preparing to save candidate CV as: {}", cv);
        repository.save(cv);
    }

    public List<JobMatch> findBestMatchForListing(String listingDescription) {
        List<JobMatch> jobMatch = repository.findAll()
                .parallelStream()
                .map(cv -> jobMatch(listingDescription, cv)).toList();
        return jobMatch
                .stream()
                .sorted(Comparator.comparing(JobMatch::match, Comparator.reverseOrder()))
                .limit(5)
                .toList();
    }

    public String justifyDecision(int match, String candidateCv, String jobListing) {
        return client.justifyDecision(match, candidateCv, jobListing);
    }

    private JobMatch jobMatch(String listingDescription, Cv cv) {
        int matchPercentage = client.determineMatch(listingDescription, cv.getCondensedText());
        logger.debug("CV ID:{} NAME:{} given a match percentage of {}%", cv.getId(), cv.getCandidateName(), matchPercentage);
        return new JobMatch(cv.getCandidateName(), matchPercentage, cv.getFullText(), cv.getFilePath());
    }
}
