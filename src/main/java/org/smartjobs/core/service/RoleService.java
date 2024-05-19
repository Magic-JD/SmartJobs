package org.smartjobs.core.service;

import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;

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

    void addCriteriaToRole(long roleId, long criteriaId);

    void removeCriteriaFromRole(long roleId, long criteriaId);
}
