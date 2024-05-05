package org.smartjobs.com.service.analysis;

import org.smartjobs.com.client.gpt.AiClient;
import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.role.data.Role;
import org.smartjobs.com.service.role.data.ScoringCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.smartjobs.com.utils.ConcurrencyUtil.virtualThreadList;

@Service
public class AnalysisService {

    private final AiClient client;


    @Autowired
    public AnalysisService(AiClient client) {
        this.client = client;
    }

    public List<ScoringCriteriaResult> scoreToCriteria(List<ProcessedCv> candidateInformation, Role role) {
        return virtualThreadList(candidateInformation, ci -> {
            List<GptClient.Score> lists = virtualThreadList(role.scoringCriteria(), sc -> client.scoreForCriteria(ci, sc));
            double totalPossibleScore = role.scoringCriteria().stream().mapToInt(ScoringCriteria::weighting).sum();
            double achievedScore = lists.stream().mapToDouble(GptClient.Score::score).sum();
            double percentage = achievedScore / totalPossibleScore * 100;
            return new ScoringCriteriaResult(UUID.randomUUID().toString(), ci.name(), percentage, lists);
        });
    }


    public record ScoringCriteriaResult(String uuid, String name, double percentage,
                                        List<GptClient.Score> scoringCriteria) {
    }




}
