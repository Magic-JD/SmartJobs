package org.smartjobs.core.service.role;

import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.ports.dal.RoleDao;
import org.smartjobs.core.ports.dal.SelectedRoleDao;
import org.smartjobs.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable("current-role")
    public Optional<Long> getCurrentlySelectedRole(String username) {
        return selectedRoleDao.getCurrentlySelectedRole(username);
    }

    @Override
    @CacheEvict("current-role")
    public void deleteCurrentlySelectedRole(String username) {
        selectedRoleDao.deleteCurrentlySelectedRole(username);
    }

    @Override
    @CacheEvict(value = "current-role", key = "#username")
    public void setCurrentlySelectedRole(String username, long roleId) {
        selectedRoleDao.setSelectedRole(username, roleId);
    }

    @Override
    @Cacheable("role")
    public Role getRole(long id) {
        return roleDao.getRoleById(id);
    }

    @Override
    @Cacheable("role-display")
    public List<RoleDisplay> getUserRoles(String username) {
        return roleDao.getUserRoles(username);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "role-display", key = "#username"),
            @CacheEvict(value = "current-role", key = "#username")
    })
    public Role createRole(String name, String username) {
        var roleId = roleDao.saveRole(username, name);
        selectedRoleDao.setSelectedRole(username, roleId);
        return new Role(roleId, name, Collections.emptyList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "role", key = "#roleId"),
            @CacheEvict(value = "role-display", key = "#candidateName")
    })
    public void deleteRole(String candidateName, long roleId) {
        roleDao.delete(roleId);
    }

    @Override
    @CacheEvict(value = "role", key = "#roleId")
    public void addCriteriaToRole(long roleId, long criteriaId) {
        roleDao.addCriteriaToRole(roleId, criteriaId);
    }

    @Override
    @CacheEvict(value = "role", key = "#roleId")
    public void removeCriteriaFromRole(long roleId, long criteriaId) {
        roleDao.removeCriteriaFromRole(roleId, criteriaId);
    }
}
