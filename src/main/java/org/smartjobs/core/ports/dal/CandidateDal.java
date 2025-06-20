package org.smartjobs.core.ports.dal;

import jakarta.transaction.Transactional;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.ProcessedCv;

import java.util.List;
import java.util.Optional;

public interface CandidateDal {
    @Transactional
    void addCvsToRepository(long userId, long roleId, List<ProcessedCv> processedCvs);

    List<CandidateData> getAllCandidates(long userId, long roleId);

    List<ProcessedCv> getAllSelected(long userId, long roleId);

    void deleteByCandidateId(long candidateId);

    Optional<CandidateData> updateCurrentlySelectedById(long cvId, boolean select);

    int findSelectedCandidateCount(long userId, long roleId);

    void deleteAllCandidates(long userId, long roleId);

    Optional<CandidateData> getByCandidateId(Long id);

    List<CandidateData> updateCurrentlySelectedAll(long userId, long roleId, boolean select);
}
