package org.smartjobs.adaptors.data.repository;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.data.RoleCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleCriteriaRepository extends JpaRepository<RoleCriteria, Long> {

    @Query(value = """
            SELECT uc.id, dc.category, dc.criteria, dc.input, uc.value, dc.is_boolean, uc.score, uc.value, dc.ai_prompt
            FROM role_criteria rc
            JOIN user_criteria uc ON rc.user_criteria_id = uc.id
            JOIN defined_criteria dc ON uc.defined_criteria_id = dc.id
            WHERE rc.role_id = :roleId
            """, nativeQuery = true)
    List<Tuple> findAllCriteriaByRoleId(Long roleId);

    @Transactional
    @Modifying
    @Query("DELETE RoleCriteria rc WHERE rc.roleId = :roleId AND rc.userCriteriaId = :userCriteriaId")
    void deleteByRoleAndCriteria(long roleId, long userCriteriaId);

    int countByRoleId(long roleId);
}
