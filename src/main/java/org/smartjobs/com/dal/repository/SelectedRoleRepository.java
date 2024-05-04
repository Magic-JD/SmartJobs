package org.smartjobs.com.dal.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.com.dal.repository.data.SelectedRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SelectedRoleRepository extends JpaRepository<SelectedRole, Long> {

    @Transactional
    Optional<SelectedRole> findByUsername(String username);

    @Transactional
    Long deleteByUsername(String username);
}
