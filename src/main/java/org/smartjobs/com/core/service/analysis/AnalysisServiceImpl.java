package org.smartjobs.com.core.service.analysis;

import org.smartjobs.com.core.client.AiClient;
import org.smartjobs.com.core.entities.*;
import org.smartjobs.com.core.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.smartjobs.com.core.utils.ConcurrencyUtil.virtualThreadList;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final AiClient client;


    @Autowired
    public AnalysisServiceImpl(AiClient client) {
        this.client = client;
    }

    @Override
    public List<ScoringCriteriaResult> scoreToCriteria(List<ProcessedCv> candidateInformation, Role role) {
        return virtualThreadList(candidateInformation, ci -> {
            List<Score> lists = virtualThreadList(role.scoringCriteria(), sc -> client.scoreForCriteria(ci, sc));
            double totalPossibleScore = role.scoringCriteria().stream().mapToInt(ScoringCriteria::weighting).sum();
            double achievedScore = lists.stream().mapToDouble(Score::score).sum();
            double percentage = achievedScore / totalPossibleScore * 100;
            return new ScoringCriteriaResult(UUID.randomUUID().toString(), ci.name(), percentage, lists);
        });
    }


}
