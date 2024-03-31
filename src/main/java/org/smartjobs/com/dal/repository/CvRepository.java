package org.smartjobs.com.dal.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.com.dal.repository.data.Cv;
import org.smartjobs.com.service.candidate.data.CandidateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CvRepository extends JpaRepository<Cv, Long> {

    @Transactional
    @Query("SELECT NEW org.smartjobs.com.service.candidate.data.CandidateData(c.id, c.candidateName, c.currentlySelected) FROM Cv c")
    List<CandidateData> findAllProjectedAsCandidateData();

    boolean existsCvByFileHash(String fileHash);

    @Transactional
    @Modifying
    @Query("UPDATE Cv c SET c.currentlySelected = :currentlySelected WHERE c.id = :id")
    void updateCurrentlySelectedById(@Param("id") long cvId, @Param("currentlySelected") boolean select);

    @Transactional
    @Query("SELECT NEW org.smartjobs.com.service.candidate.data.CandidateData(c.id, c.candidateName, c.currentlySelected) FROM Cv c WHERE c.id = :id")
    Optional<CandidateData> findCandidateDataById(@Param("id") long cvId);

    @Transactional
    List<Cv> findByCurrentlySelected(boolean currentlySelected);

    @Transactional
    int countByCurrentlySelected(boolean currentlySelected);
}
