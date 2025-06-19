package org.smartjobs.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {


    @Transactional
    @Query("""
    SELECT c.name
    FROM Analysis a
    JOIN a.candidate c
    WHERE a.id = :id
      AND a.role = c.role
      AND a.userId = c.userId
""")
    String findAnalysedCandidateName(@Param("id") long id);
}
