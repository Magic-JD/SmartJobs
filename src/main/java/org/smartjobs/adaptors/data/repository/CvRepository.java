package org.smartjobs.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.smartjobs.core.entities.CandidateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CvRepository extends JpaRepository<Cv, Long> {

    @Transactional
    @Query("SELECT NEW org.smartjobs.core.entities.CandidateData(c.id, c.candidateName, c.currentlySelected) FROM Cv c WHERE c.userId = :userId AND c.roleId = :roleId")
    List<CandidateData> findAllProjectedAsCandidateData(long userId, long roleId);

    boolean existsCvByFileHash(String fileHash);

    @Transactional
    @Modifying
    @Query("UPDATE Cv c SET c.currentlySelected = :currentlySelected WHERE c.id = :id")
    void updateCurrentlySelectedById(long id, boolean currentlySelected);

    @Transactional
    @Query("SELECT NEW org.smartjobs.core.entities.CandidateData(c.id, c.candidateName, c.currentlySelected) FROM Cv c WHERE c.id = :id")
    Optional<CandidateData> findCandidateDataById(long id);

    @Transactional
    @Query("FROM Cv c WHERE c.currentlySelected = :currentlySelected AND c.userId = :userId AND c.roleId = :roleId")
    List<Cv> findByCurrentlySelected(boolean currentlySelected, long userId, long roleId);

    @Transactional
    int countByCurrentlySelectedAndUsernameAndRoleId(boolean currentlySelected, long userId, long roleId);

    @Transactional
    void deleteByUsernameAndRoleId(long userId, long roleId);

    List<Cv> findByFileHash(String fileHash);
}
