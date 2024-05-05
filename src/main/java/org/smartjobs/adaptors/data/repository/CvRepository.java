package org.smartjobs.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.smartjobs.core.entities.CandidateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CvRepository extends JpaRepository<Cv, Long> {

    @Transactional
    @Query("SELECT NEW org.smartjobs.core.entities.CandidateData(c.id, c.candidateName, c.currentlySelected) FROM Cv c WHERE c.username = :username AND c.roleId = :roleId")
    List<CandidateData> findAllProjectedAsCandidateData(String username, long roleId);

    boolean existsCvByFileHash(String fileHash);

    @Transactional
    @Modifying
    @Query("UPDATE Cv c SET c.currentlySelected = :currentlySelected WHERE c.id = :id")
    void updateCurrentlySelectedById(@Param("id") long cvId, @Param("currentlySelected") boolean select);

    @Transactional
    @Query("SELECT NEW org.smartjobs.core.entities.CandidateData(c.id, c.candidateName, c.currentlySelected) FROM Cv c WHERE c.id = :id")
    Optional<CandidateData> findCandidateDataById(@Param("id") long cvId);

    @Transactional
    @Query("FROM Cv c WHERE c.currentlySelected = :currentlySelected AND c.username = :username AND c.roleId = :roleId")
    List<Cv> findByCurrentlySelected(boolean currentlySelected, String username, Long roleId);

    @Transactional
    int countByCurrentlySelectedAndUsernameAndRoleId(boolean currentlySelected, String username, long roleId);

    @Transactional
    void deleteByUsernameAndRoleId(String username, Long roleId);
}
