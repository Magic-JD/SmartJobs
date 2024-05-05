package org.smartjobs.core.service;

import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.UserCriteria;
import org.smartjobs.core.service.role.data.CriteriaCategory;

import java.util.List;

public interface CriteriaService {
    List<DefinedScoringCriteria> getScoringCriteriaForCategory(CriteriaCategory category);

    DefinedScoringCriteria getCriteriaById(long criteriaId);

    UserCriteria createUserCriteria(long criteriaId, String value, String score);

    void deleteUserCriteria(Long criteriaId);
}
