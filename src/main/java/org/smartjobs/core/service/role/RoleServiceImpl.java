package org.smartjobs.core.service.role;

import org.smartjobs.core.dal.RoleDao;
import org.smartjobs.core.dal.SelectedRoleDao;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    private final SelectedRoleDao selectedRoleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao, SelectedRoleDao selectedRoleDao) {
        this.roleDao = roleDao;
        this.selectedRoleDao = selectedRoleDao;
    }

    @Override
    public Optional<Long> getCurrentlySelectedRole(String username) {
        return selectedRoleDao.getCurrentlySelectedRole(username);
    }

    @Override
    public void deleteCurrentlySelectedRole(String username) {
        selectedRoleDao.deleteCurrentlySelectedRole(username);
    }

    @Override
    public void setCurrentlySelectedRole(String username, long roleId) {
        selectedRoleDao.setSelectedRole(username, roleId);
    }

    @Override
    public Role getRole(long id) {
        return roleDao.getRoleById(id);
    }

    @Override
    public List<RoleDisplay> getUserRoles(String username) {
        return roleDao.getUserRoles(username);
    }

    @Override
    public Role createRole(String name, String userId) {
        var roleId = roleDao.saveRole(userId, name);
        selectedRoleDao.setSelectedRole(userId, roleId);
        return new Role(roleId, name, Collections.emptyList());
    }

    @Override
    public void deleteRole(long roleId) {
        roleDao.delete(roleId);
    }

    @Override
    public void addCriteriaToRole(long roleId, long criteriaId) {
        roleDao.addCriteriaToRole(roleId, criteriaId);
    }

    @Override
    public void removeCriteriaFromRole(long roleId, long criteriaId) {
        roleDao.removeCriteriaFromRole(roleId, criteriaId);
    }
}
