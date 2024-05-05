package org.smartjobs.core.service;

import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.ScoringCriteriaResult;

import java.util.List;

public interface AnalysisService {
    List<ScoringCriteriaResult> scoreToCriteria(List<ProcessedCv> candidateInformation, Role role);
}
