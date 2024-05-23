package org.smartjobs.core.service;

import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.UserCriteria;
import org.smartjobs.core.service.role.data.CriteriaCategory;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Long> getCurrentlySelectedRoleId(long userId);

    Optional<Role> getCurrentlySelectedRole(long userId);

    void deleteCurrentlySelectedRole(long userId);

    void setCurrentlySelectedRole(long userId, long roleId);

    Role getRole(long id);

    List<RoleDisplay> getUserRoles(long userId);

    Role createRole(String name, long userId);

    void deleteRole(long userId, long roleId);

    void removeCriteriaFromRole(long userId, long roleId, long userCriteriaId);

    List<DefinedScoringCriteria> getScoringCriteriaForCategory(CriteriaCategory category);

    DefinedScoringCriteria getCriteriaById(long criteriaId);

    UserCriteria addUserCriteriaToRole(long definedCriteriaId, long userId, long roleId, String value, String score);

}
