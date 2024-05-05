package org.smartjobs.com.adaptors.data.repository;

import org.smartjobs.com.adaptors.data.repository.data.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByUserId(String userId);

}
