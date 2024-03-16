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

    public void updateCandidateCv(FileInformation fileInformation) {
        GptUserExtraction gptUserExtraction = client.parseCandidateData(fileInformation.fileContent());
        Cv cv = Cv.builder()
                .candidateName(gptUserExtraction.name())
                .fullText(fileInformation.fileContent())
                .condensedText(gptUserExtraction.description())
                .filePath(fileInformation.filePath())
                .build();
        logger.debug("Preparing to save candidate CV as: {}", cv);
        repository.save(cv);
    }

    public JobMatch findBestMatchForListing(String listingDescription) {
        return repository.findAll()
                .parallelStream()
                .map(cv -> jobMatch(listingDescription, cv))
                .max(Comparator.comparing(JobMatch::match))
                .orElseThrow();
    }

    public String justifyDecision(int match, String candidateCv, String jobListing) {
        return client.justifyDecision(match, candidateCv, jobListing);
    }

    private JobMatch jobMatch(String listingDescription, Cv cv) {
        int matchPercentage = client.determineMatch(listingDescription, cv.getCondensedText());
        logger.debug("CV {} given a match percentage of {}%", cv.getId(), matchPercentage);
        return new JobMatch(cv.getCandidateName(), matchPercentage, cv.getFullText(), cv.getFilePath());
    }
}
