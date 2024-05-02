package org.smartjobs.com.dal.repository;

import org.smartjobs.com.dal.repository.data.RoleCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleCriteriaRepository extends JpaRepository<RoleCriteria, Long> {

    List<RoleCriteria> findAllByRoleId(Long roleId);

}
