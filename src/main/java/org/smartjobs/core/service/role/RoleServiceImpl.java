package org.smartjobs.core.service.role;

import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.UserCriteria;
import org.smartjobs.core.exception.categories.ApplicationExceptions;
import org.smartjobs.core.exception.categories.UserResolvedExceptions;
import org.smartjobs.core.ports.dal.DefinedScoringCriteriaDao;
import org.smartjobs.core.ports.dal.RoleDao;
import org.smartjobs.core.service.RoleService;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RoleServiceImpl implements RoleService {

    public final int maxAllowedCriteria;
    private final RoleDao roleDao;
    private final DefinedScoringCriteriaDao definedScoringCriteriaDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao, DefinedScoringCriteriaDao definedScoringCriteriaDao, @Value("${role.max.criteria}") int maxAllowedCriteria) {
        this.roleDao = roleDao;
        this.definedScoringCriteriaDao = definedScoringCriteriaDao;
        this.maxAllowedCriteria = maxAllowedCriteria;
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
            @CacheEvict(value = "current-role", key = "#userId"),
            @CacheEvict(value = "role", key = "#roleId")
    })
    public void removeCriteriaFromRole(long userId, long roleId, long userCriteriaId) {
        roleDao.removeUserCriteriaFromRole(roleId, userCriteriaId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "role", key = "#roleId"),
            @CacheEvict(value = "current-role", key = "#userId"),
            @CacheEvict(value = "role-display", key = "#userId")
    })
    public void deleteRole(long userId, long roleId) {
        roleDao.delete(roleId);
    }


    @Override
    public List<DefinedScoringCriteria> getScoringCriteriaForCategory(CriteriaCategory category) {
        return getAllDefinedScoringCriteria().stream().filter(sc -> sc.category().equals(category)).toList();
    }

    @Override
    public DefinedScoringCriteria getCriteriaById(long criteriaId) {
        return getAllDefinedScoringCriteria()
                .stream()
                .filter(dc -> dc.id() == criteriaId)
                .findFirst()
                .orElseThrow((() -> new ApplicationExceptions.IncorrectIdForDefinedScoringCriteriaException(criteriaId)));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "current-role", key = "#userId"),
            @CacheEvict(value = "role", key = "#roleId")
    })
    public UserCriteria addUserCriteriaToRole(long definedCriteriaId, long userId, long roleId, String value, String score) {
        DefinedScoringCriteria definedCriteria = getCriteriaById(definedCriteriaId);
        if (!StringUtils.hasText(score)) {
            throw new UserResolvedExceptions.NoScoreProvidedException();
        }
        int scoreInt;
        try {
            scoreInt = Integer.parseInt(score);
        } catch (NumberFormatException e) {
            throw new UserResolvedExceptions.ScoreIsNotNumberException();
        }
        if (definedCriteria.needsInput() && !StringUtils.hasText(value)) {
            throw new UserResolvedExceptions.NoValueProvidedException();
        }
        UserCriteria criteria = roleDao.createNewUserCriteriaForRole(definedCriteriaId, roleId, value, scoreInt);
        if (roleDao.countCriteriaForRole(roleId) > maxAllowedCriteria) {
            roleDao.removeUserCriteriaFromRole(roleId, criteria.id());
            throw new UserResolvedExceptions.RoleCriteriaLimitReachedException(maxAllowedCriteria);
        }
        return criteria;
    }

    private List<DefinedScoringCriteria> getAllDefinedScoringCriteria() {
        return definedScoringCriteriaDao.getAllDefinedScoringCriteria();
    }
}
