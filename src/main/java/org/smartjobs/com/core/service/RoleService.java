package org.smartjobs.com.core.service;

import org.smartjobs.com.core.entities.Role;
import org.smartjobs.com.core.entities.RoleDisplay;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Long> getCurrentlySelectedRole(String username);

    void deleteCurrentlySelectedRole(String username);

    void setCurrentlySelectedRole(String username, long roleId);

    Role getRole(long id);

    List<RoleDisplay> getUserRoles(String username);

    Role createRole(String name, String userId);

    void deleteRole(long roleId);

    void addCriteriaToRole(long roleId, long criteriaId);

    void removeCriteriaFromRole(long roleId, long criteriaId);
}
