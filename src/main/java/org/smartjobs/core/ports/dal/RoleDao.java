package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.UserCriteria;

import java.util.List;
import java.util.Optional;

public interface RoleDao {
    List<RoleDisplay> getUserRoles(String username);

    long saveRole(String userId, String name);

    Role getRoleById(long id);

    void delete(long roleId);

    void addCriteriaToRole(long roleId, long criteriaId);

    void removeCriteriaFromRole(long roleId, long criteriaId);

    void setSelectedRole(String username, Long roleId);

    Optional<Long> getCurrentlySelectedRoleById(String username);

    void deleteCurrentlySelectedRole(String username);

    Optional<Role> getCurrentlySelectedRole(String username);

    UserCriteria createNewUserCriteria(long definedCriteriaId, String value, int score);

    void deleteUserCriteria(Long criteriaId);
}
