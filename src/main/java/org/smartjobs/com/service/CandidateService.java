package org.smartjobs.com.service;

import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.data.Cv;
import org.smartjobs.com.repository.CvDAO;
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

    public void updateCandidateCv(String candidateName, String cvData) {
        String condensedCvData = client.condenseCvData(cvData);
        Cv cv = Cv.builder()
                .text(condensedCvData)
                .candidateName(candidateName)
                .build();
        repository.save(cv);
    }

    public String findBestMatchForListing(String listingDescription) {
        return repository.findAll()
                .parallelStream()
                .map(cv -> new Matched(cv.getCandidateName(), client.determineMatch(listingDescription, cv.getText())))
                .max(Comparator.comparing(Matched::match))
                .map(Matched::name)
                .orElse("There are no CVs provided. Please provide CVs and try again.");
    }

    public String evaluateCv(String cv) {
        return client.evaluateCv(cv);
    }

    private record Matched(String name, int match) {
    }
}
