package org.smartjobs.com.dal.repository;

import org.smartjobs.com.dal.repository.data.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByUsername(String username);

}
