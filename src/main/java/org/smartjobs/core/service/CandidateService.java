package org.smartjobs.core.service;

import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.entities.ProcessedCv;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    List<ProcessedCv> getFullCandidateInfo(long userId, long roleId);

    List<CandidateData> getCurrentCandidates(long userId, long role);

    void updateCandidateCvs(long userId, long roleId, List<Optional<FileInformation>> fileInformationList);

    void deleteCandidate(long userId, long currentRole, long cvId);

    Optional<CandidateData> toggleCandidateSelect(long userId, long roleId, long cvId, boolean select);

    int findSelectedCandidateCount(long userId, long currentRole);

    void deleteAllCandidates(long userId, long roleId);
}
