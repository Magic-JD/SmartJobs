package org.smartjobs.core.service;

import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.entities.ProcessedCv;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    List<ProcessedCv> getFullCandidateInfo(String userName, Long roleId);

    List<CandidateData> getCurrentCandidates(String userName, Long role);

    void updateCandidateCvs(String username, Long roleId, List<Optional<FileInformation>> fileInformationList);

    void deleteCandidate(String username, long cvId);

    Optional<CandidateData> toggleCandidateSelect(String currentUsername, long cvId, boolean select);

    int findSelectedCandidateCount(String username, long currentRole);

    void deleteAllCandidates(String username, Long roleId);
}
