package org.smartjobs.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.SelectedRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SelectedRoleRepository extends JpaRepository<SelectedRole, Long> {

    @Transactional
    Optional<SelectedRole> findByUserId(long userId);

    @Transactional
    Long deleteByUserId(long userId);

    @Query("SELECT sr.role.id FROM SelectedRole sr WHERE sr.userId = :userId")
    Optional<Long> findRoleIdByUserId(long userId);
}
