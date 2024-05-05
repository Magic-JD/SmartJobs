package org.smartjobs.com.core.dal;

import jakarta.transaction.Transactional;
import org.smartjobs.com.core.entities.CandidateData;
import org.smartjobs.com.core.entities.ProcessedCv;

import java.util.List;
import java.util.Optional;

public interface CvDao {
    @Transactional
    void addCvsToRepository(String username, long roleId, List<ProcessedCv> processedCvs);

    List<CandidateData> getAllNames(String userName, Long roleId);

    List<ProcessedCv> getAllSelected(String userName, Long roleId);

    void deleteByCvId(long cvId);

    boolean knownHash(String fileHash);

    Optional<CandidateData> updateCurrentlySelectedById(long cvId, boolean select);

    int findSelectedCandidateCount(String username, long roleId);

    void deleteAllCandidates(String username, Long roleId);
}
