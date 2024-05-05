package org.smartjobs.com.core.dal;

import org.smartjobs.com.core.entities.UserCriteria;

import java.util.List;

public interface UserCriteriaDao {
    UserCriteria createNewUserCriteria(long definedCriteriaId, String value, int score);

    List<UserCriteria> selectUserCriteriaByIds(List<Long> ids);

    void deleteUserCriteria(Long criteriaId);
}
