package org.smartjobs.core.service.role;

import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.UserCriteria;
import org.smartjobs.core.exception.categories.ApplicationExceptions;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoScoreProvidedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoValueProvidedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleCriteriaLimitReachedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.ScoreIsNotNumberException;
import org.smartjobs.core.ports.dal.RoleDal;
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
    private final RoleDal roleDal;

    @Autowired
    public RoleServiceImpl(RoleDal roleDal, @Value("${role.max.criteria}") int maxAllowedCriteria) {
        this.roleDal = roleDal;
        this.maxAllowedCriteria = maxAllowedCriteria;
    }

    @Override
    @Cacheable("current-role-id")
    public Optional<Long> getCurrentlySelectedRoleId(long userId) {
        return roleDal.getCurrentlySelectedRoleByUserId(userId);
    }

    @Override
    @Cacheable("current-role")
    public Optional<Role> getCurrentlySelectedRole(long userId) {
        return roleDal.getCurrentlySelectedRole(userId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "current-role-id"),
            @CacheEvict(value = "current-role")
    })
    public void deleteCurrentlySelectedRole(long userId) {
        roleDal.deleteCurrentlySelectedRole(userId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "current-role-id", key = "#userId"),
            @CacheEvict(value = "current-role", key = "#userId")
    })
    public void setCurrentlySelectedRole(long userId, long roleId) {
        roleDal.setSelectedRole(userId, roleId);
    }

    @Override
    @Cacheable("role")
    public Role getRole(long id) {
        return roleDal.getRoleById(id);
    }

    @Override
    @Cacheable("role-display")
    public List<RoleDisplay> getUserRoles(long userId) {
        return roleDal.getUserRoles(userId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "role-display", key = "#username"),
            @CacheEvict(value = "current-role-id", key = "#username"),
            @CacheEvict(value = "current-role", key = "#username")
    })
    public Role createRole(String name, long username) {
        var roleId = roleDal.saveRole(username, name);
        roleDal.setSelectedRole(username, roleId);
        return new Role(roleId, name, Collections.emptyList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "current-role", key = "#userId"),
            @CacheEvict(value = "role", key = "#roleId")
    })
    public void removeCriteriaFromRole(long userId, long roleId, long userCriteriaId) {
        roleDal.removeUserCriteriaFromRole(roleId, userCriteriaId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "role", key = "#roleId"),
            @CacheEvict(value = "current-role", key = "#userId"),
            @CacheEvict(value = "role-display", key = "#userId")
    })
    public void deleteRole(long userId, long roleId) {
        roleDal.deleteRole(roleId);
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
            throw new NoScoreProvidedException(userId);
        }
        int scoreInt;
        try {
            scoreInt = Integer.parseInt(score);
        } catch (NumberFormatException e) {
            throw new ScoreIsNotNumberException(userId);
        }
        if (definedCriteria.needsInput() && !StringUtils.hasText(value)) {
            throw new NoValueProvidedException(userId);
        }
        UserCriteria criteria = roleDal.createNewUserCriteriaForRole(definedCriteriaId, roleId, value, scoreInt);
        if (roleDal.countCriteriaForRole(roleId) > maxAllowedCriteria) {
            roleDal.removeUserCriteriaFromRole(roleId, criteria.id());
            throw new RoleCriteriaLimitReachedException(userId, maxAllowedCriteria);
        }
        return criteria;
    }

    private List<DefinedScoringCriteria> getAllDefinedScoringCriteria() {
        return roleDal.getAllDefinedScoringCriteria();
    }
}
