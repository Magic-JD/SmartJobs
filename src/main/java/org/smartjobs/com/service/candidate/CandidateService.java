package org.smartjobs.com.service.candidate;

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
        repository.save(cv);
    }

    public JobMatch findBestMatchForListing(String listingDescription) {
        return repository.findAll()
                .parallelStream()
                .map(cv -> new JobMatch(cv.getCandidateName(), client.determineMatch(listingDescription, cv.getCondensedText()), cv.getFullText(), cv.getFilePath()))
                .max(Comparator.comparing(JobMatch::match))
                .orElseThrow();
    }

    public String justifyDecision(int match, String candidateCv, String jobListing) {
        return client.justifyDecision(match, candidateCv, jobListing);
    }
}
