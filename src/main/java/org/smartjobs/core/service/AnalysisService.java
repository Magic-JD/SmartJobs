package org.smartjobs.core.service;

import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.Role;

import java.util.List;

public interface AnalysisService {
    List<CandidateScores> scoreToCriteria(String username, List<ProcessedCv> candidateInformation, Role role);
}
