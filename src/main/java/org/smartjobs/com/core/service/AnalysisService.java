package org.smartjobs.com.core.service;

import org.smartjobs.com.core.entities.ProcessedCv;
import org.smartjobs.com.core.entities.Role;
import org.smartjobs.com.core.entities.ScoringCriteriaResult;

import java.util.List;

public interface AnalysisService {
    List<ScoringCriteriaResult> scoreToCriteria(List<ProcessedCv> candidateInformation, Role role);
}
