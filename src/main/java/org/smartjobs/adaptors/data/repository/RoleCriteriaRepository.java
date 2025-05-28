package org.smartjobs.adaptors.data.repository;

import org.smartjobs.adaptors.data.repository.data.RoleCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleCriteriaRepository extends JpaRepository<RoleCriteria, Long> {

    int countByRoleId(long roleId);
}
