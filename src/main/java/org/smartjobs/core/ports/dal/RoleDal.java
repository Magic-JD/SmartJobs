package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.UserCriteria;

import java.util.List;
import java.util.Optional;

public interface RoleDal {
    List<RoleDisplay> getUserRoles(long userId);

    long saveRole(long userId, String name);

    Role getRoleById(long id);

    void delete(long roleId);

    void removeUserCriteriaFromRole(long roleId, long userCriteriaId);

    void setSelectedRole(long userId, long roleId);

    Optional<Long> getCurrentlySelectedRoleById(long userId);

    void deleteCurrentlySelectedRole(long userId);

    Optional<Role> getCurrentlySelectedRole(long userId);

    UserCriteria createNewUserCriteriaForRole(long definedCriteriaId, long roleId, String value, int score);

    int countCriteriaForRole(long roleId);

    List<DefinedScoringCriteria> getAllDefinedScoringCriteria();
}
