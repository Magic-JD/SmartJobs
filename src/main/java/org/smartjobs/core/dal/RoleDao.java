package org.smartjobs.core.dal;

import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;

import java.util.List;

public interface RoleDao {
    List<RoleDisplay> getUserRoles(String username);

    long saveRole(String userId, String name);

    Role getRoleById(long id);

    void delete(long roleId);

    void addCriteriaToRole(long roleId, long criteriaId);

    void removeCriteriaFromRole(long roleId, long criteriaId);
}
