package org.smartjobs.com.service.role;

import org.smartjobs.com.dal.RoleDao;
import org.smartjobs.com.dal.SelectedRoleDao;
import org.smartjobs.com.service.role.data.Role;
import org.smartjobs.com.service.role.data.RoleDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleDao roleDao;

    private final SelectedRoleDao selectedRoleDao;

    @Autowired
    public RoleService(RoleDao roleDao, SelectedRoleDao selectedRoleDao) {
        this.roleDao = roleDao;
        this.selectedRoleDao = selectedRoleDao;
    }

    public Optional<Long> getCurrentlySelectedRole(String username) {
        return selectedRoleDao.getCurrentlySelectedRole(username);
    }

    public void setCurrentlySelectedRole(String username, long roleId) {
        selectedRoleDao.setSelectedRole(username, roleId);
    }

    public Role getRole(long id) {
        return roleDao.getRoleById(id);
    }

    public List<RoleDisplay> getUserRoles(String username) {
        return roleDao.getUserRoles(username);
    }

    public Role createRole(String name, String userId) {
        var role = roleDao.saveRole(userId, name);
        selectedRoleDao.setSelectedRole(userId, role.getId());
        return new Role(role.getId(), role.getPosition(), Collections.emptyList());
    }

    public void deleteRole(long roleId) {
        roleDao.delete(roleId);
    }

    public void addCriteriaToRole(long roleId, long criteriaId) {
        roleDao.addCriteriaToRole(roleId, criteriaId);
    }
}
