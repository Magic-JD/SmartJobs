package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.AnalysisRepository;
import org.smartjobs.adaptors.data.repository.CriteriaAnalysisRepository;
import org.smartjobs.adaptors.data.repository.data.Analysis;
import org.smartjobs.adaptors.data.repository.data.CriteriaAnalysis;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ScoredCriteria;
import org.smartjobs.core.ports.dal.AnalysisDal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Component
public class AnalysisDalImpl implements AnalysisDal {

    private final CriteriaAnalysisRepository criteriaAnalysisRepository;
    private final AnalysisRepository analysisRepository;

    @Autowired
    public AnalysisDalImpl(CriteriaAnalysisRepository criteriaAnalysisRepository, AnalysisRepository analysisRepository) {
        this.criteriaAnalysisRepository = criteriaAnalysisRepository;
        this.analysisRepository = analysisRepository;
    }

    @Override
    public CandidateScores getResultById(long id) {
        var nameF = supplyAsync(() -> analysisRepository.findAnalysedCandidateName(id));
        var analysisListF = supplyAsync(() -> criteriaAnalysisRepository.findAllByAnalysisId(id));
        var name = nameF.join();
        var analysisList = analysisListF.join();
        return new CandidateScores(id, name, analysisList.stream()
                .map(ca -> new ScoredCriteria(ca.getUserCriteriaId(), ca.getCriteriaRequest(), ca.getDescription(), ca.getScore(), ca.getMaxScore()))
                .toList());
    }

    @Override
    public long saveResults(long userId, long cvId, long roleId, List<ScoredCriteria> clearResults) {
        Analysis savedAnalysis = analysisRepository.save(Analysis.builder().cvId(cvId).roleId(roleId).userId(userId).build());
        long analysisId = savedAnalysis.getId();
        criteriaAnalysisRepository.saveAll(clearResults
                .stream()
                .map(scoredCriteria -> CriteriaAnalysis.builder()
                        .analysisId(analysisId)
                        .score(scoredCriteria.score())
                        .userCriteriaId(scoredCriteria.userCriteriaId())
                        .maxScore(scoredCriteria.maxScore())
                        .criteriaRequest(scoredCriteria.criteriaRequest())
                        .description(scoredCriteria.justification())
                        .build())
                .toList());
        return analysisId;
    }
}
