package org.smartjobs.com.core.dal;

import org.smartjobs.com.core.service.criteria.CriteriaService;

import java.util.List;

public interface UserCriteriaDao {
    CriteriaService.UserCriteria createNewUserCriteria(long definedCriteriaId, String value, int score);

    List<CriteriaService.UserCriteria> selectUserCriteriaByIds(List<Long> ids);

    void deleteUserCriteria(Long criteriaId);
}
