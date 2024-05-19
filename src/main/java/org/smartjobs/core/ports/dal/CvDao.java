package org.smartjobs.core.ports.dal;

import jakarta.transaction.Transactional;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.ProcessedCv;

import java.util.List;
import java.util.Optional;

public interface CvDao {
    @Transactional
    void addCvsToRepository(long userId, long roleId, List<ProcessedCv> processedCvs);

    List<CandidateData> getAllNames(long userId, long roleId);

    List<ProcessedCv> getAllSelected(long userId, long roleId);

    void deleteByCvId(long cvId);

    boolean knownHash(String fileHash);

    Optional<CandidateData> updateCurrentlySelectedById(long cvId, boolean select);

    int findSelectedCandidateCount(long userId, long roleId);

    void deleteAllCandidates(long userId, long roleId);

    Optional<ProcessedCv> getByHash(String hash);
}
