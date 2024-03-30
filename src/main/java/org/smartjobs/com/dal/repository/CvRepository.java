package org.smartjobs.com.dal.repository;

import org.smartjobs.com.dal.repository.data.Cv;
import org.smartjobs.com.service.candidate.data.CandidateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CvRepository extends JpaRepository<Cv, Long> {

    @Query("SELECT NEW org.smartjobs.com.service.candidate.data.CandidateData(c.id, c.candidateName) FROM Cv c")
    List<CandidateData> findAllProjectedAsCandidateData();

    boolean existsCvByFileHash(String fileHash);
}
