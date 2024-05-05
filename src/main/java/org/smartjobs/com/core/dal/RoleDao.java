package org.smartjobs.com.core.dal;

import org.smartjobs.com.adaptors.data.repository.data.Role;
import org.smartjobs.com.core.entities.RoleDisplay;

import java.util.List;

public interface RoleDao {
    List<RoleDisplay> getUserRoles(String username);

    Role saveRole(String userId, String name);

    org.smartjobs.com.core.entities.Role getRoleById(long id);

    void delete(long roleId);

    void addCriteriaToRole(long roleId, long criteriaId);

    void removeCriteriaFromRole(long roleId, long criteriaId);
}
