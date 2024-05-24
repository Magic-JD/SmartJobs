package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.AnalysisRepository;
import org.smartjobs.adaptors.data.repository.CandidateRepository;
import org.smartjobs.adaptors.data.repository.CriteriaAnalysisRepository;
import org.smartjobs.adaptors.data.repository.CvRepository;
import org.smartjobs.adaptors.data.repository.data.Analysis;
import org.smartjobs.adaptors.data.repository.data.Candidate;
import org.smartjobs.adaptors.data.repository.data.CriteriaAnalysis;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.smartjobs.core.entities.ScoredCriteria;
import org.smartjobs.core.ports.dal.AnalysisDal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnalysisDalImpl implements AnalysisDal {

    private final CriteriaAnalysisRepository criteriaAnalysisRepository;
    private final AnalysisRepository analysisRepository;
    private final CvRepository cvRepository;
    private final CandidateRepository candidateRepository;

    @Autowired
    public AnalysisDalImpl(CriteriaAnalysisRepository criteriaAnalysisRepository, AnalysisRepository analysisRepository, CvRepository cvRepository, CandidateRepository candidateRepository) {
        this.criteriaAnalysisRepository = criteriaAnalysisRepository;
        this.analysisRepository = analysisRepository;
        this.cvRepository = cvRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    public CandidateDisplay getResultById(long id) {
        Analysis analysis = analysisRepository.getReferenceById(id);
        Cv cv = cvRepository.getReferenceById(analysis.getCvId());
        Candidate candidate = candidateRepository.findAllByCvId(cv.getId()).stream()
                .filter(c -> c.getUserId().equals(analysis.getUserId()) && c.getRoleId().equals(analysis.getRoleId()))
                .findFirst()
                .orElseThrow();
        List<CriteriaAnalysis> analysisList = criteriaAnalysisRepository.findAllByAnalysisId(id);
        return new CandidateDisplay(id, candidate.getName(), analysisList.stream().map(ca -> new ScoreResults(ca.getCriteriaRequest(), ca.getDescription(), ca.getScore(), ca.getMaxScore())).toList());
    }

    public record CandidateDisplay(long id, String name, List<ScoreResults> results) {
    }

    public record ScoreResults(String criteriaRequest, String description, double score, int maxScore) {
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
