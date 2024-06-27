package org.smartjobs.core.service;

import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.ProcessedCv;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    List<ProcessedCv> getFullCandidateInfo(long userId, long roleId);

    List<CandidateData> getCurrentCandidates(long userId, long roleId);

    List<ProcessedCv> updateCandidateCvs(long userId, long roleId, List<MultipartFile> files);

    void deleteCandidate(long userId, long roleId, long candidateId);

    Optional<CandidateData> toggleCandidateSelect(long userId, long roleId, long cvId, boolean select);

    List<CandidateData> toggleCandidateSelectAll(long userId, long roleId, boolean select);

    int findSelectedCandidateCount(long userId, long roleId);

    void deleteAllCandidates(long userId, long roleId);
}
