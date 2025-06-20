package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.AnalysisRepository;
import org.smartjobs.adaptors.data.repository.CandidateRepository;
import org.smartjobs.adaptors.data.repository.CriteriaAnalysisRepository;
import org.smartjobs.adaptors.data.repository.RoleRepository;
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
    private final CandidateRepository candidateRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AnalysisDalImpl(CriteriaAnalysisRepository criteriaAnalysisRepository, AnalysisRepository analysisRepository, CandidateRepository candidateRepository, RoleRepository roleRepository) {
        this.criteriaAnalysisRepository = criteriaAnalysisRepository;
        this.analysisRepository = analysisRepository;
        this.candidateRepository = candidateRepository;
        this.roleRepository = roleRepository;
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
    public long saveResults(long userId, long candidateId, long roleId, List<ScoredCriteria> clearResults) {
        var candidate = candidateRepository.getReferenceById(candidateId);
        var role = roleRepository.getReferenceById(roleId);
        Analysis analysis = analysisRepository.save(Analysis.builder().candidate(candidate).role(role).userId(userId).build());
        criteriaAnalysisRepository.saveAll(clearResults
                .stream()
                .map(scoredCriteria -> CriteriaAnalysis.builder()
                        .analysis(analysis)
                        .score(scoredCriteria.score())
                        .userCriteriaId(scoredCriteria.userCriteriaId())
                        .maxScore(scoredCriteria.maxScore())
                        .criteriaRequest(scoredCriteria.criteriaRequest())
                        .description(scoredCriteria.justification())
                        .build())
                .toList());
        return analysis.getId();
    }
}
