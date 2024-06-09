package org.smartjobs.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {


    @Transactional
    @Query(value = """
            SELECT c.name
            FROM analysis a
            JOIN cv cv ON a.cv_id = cv.id
            JOIN candidate c on cv.id = c.cv_id
            WHERE a.id = :id
            AND a.role_id = c.role_id
            AND a.user_id = c.user_id
            LIMIT 1
            """,
            nativeQuery = true)
    String findAnalysedCandidateName(long id);
}
