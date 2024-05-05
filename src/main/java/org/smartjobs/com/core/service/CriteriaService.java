package org.smartjobs.com.core.service;

import org.smartjobs.com.core.entities.DefinedScoringCriteria;
import org.smartjobs.com.core.entities.UserCriteria;
import org.smartjobs.com.core.service.role.data.CriteriaCategory;

import java.util.List;

public interface CriteriaService {
    List<DefinedScoringCriteria> getScoringCriteriaForCategory(CriteriaCategory category);

    DefinedScoringCriteria getCriteriaById(long criteriaId);

    UserCriteria createUserCriteria(long criteriaId, String value, String score);

    void deleteUserCriteria(Long criteriaId);
}
