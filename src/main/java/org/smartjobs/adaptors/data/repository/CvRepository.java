package org.smartjobs.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CvRepository extends JpaRepository<Cv, Long> {

    boolean existsCvByFileHash(String fileHash);


    @Transactional
    @Query("""
    SELECT cv
    FROM Cv cv
    JOIN Candidate c ON cv.id = c.cv.id
    WHERE c.currentlySelected = :currentlySelected
    AND c.userId = :userId
    AND c.role.id = :roleId
    """)
    List<Cv> findByCurrentlySelected(@Param("currentlySelected") boolean currentlySelected,
                                              @Param("userId") long userId,
                                              @Param("roleId") long roleId);


    @Transactional
    Optional<Cv> findByFileHash(String fileHash);
}
