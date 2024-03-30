package org.smartjobs.com.service.analysis;

import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.smartjobs.com.utils.ConcurrencyUtil.virtualThreadList;

@Service
public class AnalysisService {

    private final GptClient client;


    @Autowired
    public AnalysisService(GptClient client) {
        this.client = client;
    }

    public List<GptClient.ScoringCriteriaResult> scoreToCriteria(List<ProcessedCv> candidateInformation) {
        return virtualThreadList(candidateInformation, client::scoreToCriteria);
    }


}
