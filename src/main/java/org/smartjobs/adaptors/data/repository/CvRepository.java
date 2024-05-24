package org.smartjobs.adaptors.data.repository;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CvRepository extends JpaRepository<Cv, Long> {

    boolean existsCvByFileHash(String fileHash);


    @Transactional
    @Query(value = """
            SELECT cv.id, c.name, cv.condensed_text, cv.file_hash
            FROM cv cv
            JOIN candidate c ON cv.id = c.cv_id
            WHERE c.currently_selected = :currentlySelected
            AND c.user_id = :userId
            AND c.role_id = :roleId
            """,
            nativeQuery = true)
    List<Tuple> findByCurrentlySelected(boolean currentlySelected, long userId, long roleId);


    @Transactional
    List<Cv> findByFileHash(String fileHash);
}
