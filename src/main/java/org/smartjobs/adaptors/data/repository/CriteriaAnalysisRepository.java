package org.smartjobs.adaptors.data.repository;

import org.smartjobs.adaptors.data.repository.data.CriteriaAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriteriaAnalysisRepository extends JpaRepository<CriteriaAnalysis, Long> {
    List<CriteriaAnalysis> findAllByAnalysisId(long analysisId);
}
