package org.smartjobs.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @Transactional
    List<Candidate> findAllByUserIdAndRoleId(long userId, long roleId);

    @Transactional
    @Query(value = """
            UPDATE candidate
            SET currently_selected = :currentlySelected
            WHERE id = :id
            RETURNING *
            """,
            nativeQuery = true)
    Optional<Candidate> updateCurrentlySelectedById(long id, boolean currentlySelected);

    @Transactional
    @Query(value = """
            UPDATE candidate
            SET currently_selected = :currentlySelected
            WHERE user_id = :userId
            AND role_id = :roleId
            RETURNING *
            """,
            nativeQuery = true)
    List<Candidate> updateCurrentlySelectedAll(long userId, long roleId, boolean currentlySelected);

    @Transactional
    int countByCurrentlySelectedAndUserIdAndRoleId(boolean currentlySelected, long userId, long roleId);

    @Transactional
    void deleteByUserIdAndRoleId(long userId, long roleId);

    List<Candidate> findAllByCvId(Long cvId);
}
