package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.DefinedScoringCriteriaRepository;
import org.smartjobs.adaptors.data.repository.RoleCriteriaRepository;
import org.smartjobs.adaptors.data.repository.RoleRepository;
import org.smartjobs.adaptors.data.repository.UserCriteriaRepository;
import org.smartjobs.adaptors.data.repository.data.Criteria;
import org.smartjobs.adaptors.data.repository.data.Role;
import org.smartjobs.adaptors.data.repository.data.RoleCriteria;
import org.smartjobs.core.dal.RoleDao;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.ScoringCriteria;
import org.smartjobs.core.exception.categories.ApplicationExceptions.IncorrectIdForRoleRetrievalException;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoleDaoImpl implements RoleDao {

    private final RoleRepository roleRepository;
    private final RoleCriteriaRepository roleCriteriaRepository;
    private final UserCriteriaRepository userCriteriaRepository;
    private final DefinedScoringCriteriaRepository definedScoringCriteriaRepository;

    @Autowired
    public RoleDaoImpl(RoleRepository roleRepository, RoleCriteriaRepository roleCriteriaRepository, UserCriteriaRepository userCriteriaRepository, DefinedScoringCriteriaRepository definedScoringCriteriaRepository) {
        this.roleRepository = roleRepository;
        this.roleCriteriaRepository = roleCriteriaRepository;
        this.userCriteriaRepository = userCriteriaRepository;
        this.definedScoringCriteriaRepository = definedScoringCriteriaRepository;
    }

    @Override
    public List<RoleDisplay> getUserRoles(String username) {
        return roleRepository.findByUserId(username).stream()
                .map(role -> new RoleDisplay(role.getId(), role.getPosition()))
                .toList();
    }

    @Override
    public long saveRole(String userId, String name) {
        return roleRepository.saveAndFlush(Role.builder().userId(userId).position(name).build()).getId();
    }

    @Override
    public org.smartjobs.core.entities.Role getRoleById(long id) {
        var criteria = roleCriteriaRepository.findAllByRoleId(id).stream()
                .map(rc -> userCriteriaRepository.findById(rc.getCriteriaId())).map(Optional::orElseThrow).map(userCriteria -> {
                    Criteria defCrit = definedScoringCriteriaRepository.findById(userCriteria.getDefinedCriteriaId()).orElseThrow();
                    return new ScoringCriteria(userCriteria.getId(), CriteriaCategory.getFromName(defCrit.getCategory()), defCrit.getCriteria() + (defCrit.isInput() ? ": " + userCriteria.getValue() : ""), userCriteria.getScore(), defCrit.getAiPrompt().replaceAll("X", userCriteria.getValue()));
                }).toList();
        return roleRepository.findById(id)
                .map(role -> new org.smartjobs.core.entities.Role(role.getId(), role.getPosition(), criteria))
                .orElseThrow(() -> new IncorrectIdForRoleRetrievalException(id));
    }

    @Override
    public void delete(long roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    public void addCriteriaToRole(long roleId, long criteriaId) {
        roleCriteriaRepository.save(RoleCriteria.builder().roleId(roleId).criteriaId(criteriaId).build());
    }

    @Override
    public void removeCriteriaFromRole(long roleId, long criteriaId) {
        roleCriteriaRepository.deleteByRoleAndCriteria(roleId, criteriaId);
    }
}
