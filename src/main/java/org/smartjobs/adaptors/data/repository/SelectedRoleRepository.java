package org.smartjobs.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.SelectedRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SelectedRoleRepository extends JpaRepository<SelectedRole, Long> {

    @Transactional
    Optional<SelectedRole> findByUsername(long userId);

    @Transactional
    Long deleteByUserId(long userId);
}
