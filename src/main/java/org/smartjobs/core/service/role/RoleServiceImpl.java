package org.smartjobs.core.service.role;

import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.ports.dal.RoleDao;
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


    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Cacheable("current-role-id")
    public Optional<Long> getCurrentlySelectedRoleId(long userId) {
        return roleDao.getCurrentlySelectedRoleById(userId);
    }

    @Override
    @Cacheable("current-role")
    public Optional<Role> getCurrentlySelectedRole(long userId) {
        return roleDao.getCurrentlySelectedRole(userId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "current-role-id"),
            @CacheEvict(value = "current-role")
    })
    public void deleteCurrentlySelectedRole(long userId) {
        roleDao.deleteCurrentlySelectedRole(userId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "current-role-id", key = "#userId"),
            @CacheEvict(value = "current-role", key = "#userId")
    })
    public void setCurrentlySelectedRole(long userId, long roleId) {
        roleDao.setSelectedRole(userId, roleId);
    }

    @Override
    @Cacheable("role")
    public Role getRole(long id) {
        return roleDao.getRoleById(id);
    }

    @Override
    @Cacheable("role-display")
    public List<RoleDisplay> getUserRoles(long userId) {
        return roleDao.getUserRoles(userId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "role-display", key = "#username"),
            @CacheEvict(value = "current-role-id", key = "#username"),
            @CacheEvict(value = "current-role", key = "#username")
    })
    public Role createRole(String name, long username) {
        var roleId = roleDao.saveRole(username, name);
        roleDao.setSelectedRole(username, roleId);
        return new Role(roleId, name, Collections.emptyList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "role", key = "#roleId"),
            @CacheEvict(value = "role-display", key = "#candidateName")
    })
    public void deleteRole(long candidateName, long roleId) {
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
