package org.smartjobs.com.dal.repository;

import org.smartjobs.com.dal.repository.data.SelectedRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SelectedRoleRepository extends JpaRepository<SelectedRole, Long> {

    Optional<SelectedRole> findByUsername(String username);

}
