package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.UserCriteria;

import java.util.List;
import java.util.Optional;

public interface RoleDao {
    List<RoleDisplay> getUserRoles(long userId);

    long saveRole(long userId, String name);

    Role getRoleById(long id);

    void delete(long roleId);

    void addCriteriaToRole(long roleId, long criteriaId);

    void removeCriteriaFromRole(long roleId, long criteriaId);

    void setSelectedRole(long userId, long roleId);

    Optional<Long> getCurrentlySelectedRoleById(long userId);

    void deleteCurrentlySelectedRole(long userId);

    Optional<Role> getCurrentlySelectedRole(long userId);

    UserCriteria createNewUserCriteria(long definedCriteriaId, String value, int score);

    void deleteUserCriteria(long criteriaId);
}
