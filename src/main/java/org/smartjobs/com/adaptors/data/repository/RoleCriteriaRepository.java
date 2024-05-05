package org.smartjobs.com.adaptors.data.repository;

import jakarta.transaction.Transactional;
import org.smartjobs.com.adaptors.data.repository.data.RoleCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleCriteriaRepository extends JpaRepository<RoleCriteria, Long> {

    List<RoleCriteria> findAllByRoleId(Long roleId);

    @Transactional
    @Modifying
    @Query("DELETE RoleCriteria rc WHERE rc.roleId = :roleId AND rc.criteriaId = :criteriaId")
    void deleteByRoleAndCriteria(long roleId, long criteriaId);
}
