package org.smartjobs.com.dal;

import org.smartjobs.com.dal.repository.DefinedScoringCriteriaRepository;
import org.smartjobs.com.dal.repository.RoleCriteriaRepository;
import org.smartjobs.com.dal.repository.RoleRepository;
import org.smartjobs.com.dal.repository.UserCriteriaRepository;
import org.smartjobs.com.dal.repository.data.Criteria;
import org.smartjobs.com.dal.repository.data.Role;
import org.smartjobs.com.dal.repository.data.RoleCriteria;
import org.smartjobs.com.exception.categories.ApplicationExceptions.IncorrectIdForRoleRetrievalException;
import org.smartjobs.com.service.role.data.CriteriaCategory;
import org.smartjobs.com.service.role.data.RoleDisplay;
import org.smartjobs.com.service.role.data.ScoringCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoleDao {

    private final RoleRepository roleRepository;
    private final RoleCriteriaRepository roleCriteriaRepository;
    private final UserCriteriaRepository userCriteriaRepository;
    private final DefinedScoringCriteriaRepository definedScoringCriteriaRepository;

    @Autowired
    public RoleDao(RoleRepository roleRepository, RoleCriteriaRepository roleCriteriaRepository, UserCriteriaRepository userCriteriaRepository, DefinedScoringCriteriaRepository definedScoringCriteriaRepository) {
        this.roleRepository = roleRepository;
        this.roleCriteriaRepository = roleCriteriaRepository;
        this.userCriteriaRepository = userCriteriaRepository;
        this.definedScoringCriteriaRepository = definedScoringCriteriaRepository;
    }

    public List<RoleDisplay> getUserRoles(String username) {
        return roleRepository.findByUserId(username).stream()
                .map(role -> new RoleDisplay(role.getId(), role.getPosition()))
                .toList();
    }

    public Role saveRole(String userId, String name) {
        return roleRepository.saveAndFlush(Role.builder().userId(userId).position(name).build());
    }

    public org.smartjobs.com.service.role.data.Role getRoleById(long id) {
        var criteria = roleCriteriaRepository.findAllByRoleId(id).stream()
                .map(rc -> userCriteriaRepository.findById(rc.getCriteriaId())).map(Optional::orElseThrow).map(userCriteria -> {
                    Criteria defCrit = definedScoringCriteriaRepository.findById(userCriteria.getDefinedCriteriaId()).orElseThrow();
                    return new ScoringCriteria(CriteriaCategory.getFromName(defCrit.getCategory()), defCrit.getCriteria() + (defCrit.isInput() ? ": " + userCriteria.getValue() : ""), userCriteria.getScore(), defCrit.getAiPrompt().replaceAll("X", userCriteria.getValue()));
                }).toList();
        return roleRepository.findById(id)
                .map(role -> new org.smartjobs.com.service.role.data.Role(role.getId(), role.getPosition(), criteria))
                .orElseThrow(() -> new IncorrectIdForRoleRetrievalException(id));
    }

    public void delete(long roleId) {
        roleRepository.deleteById(roleId);
    }

    public void addCriteriaToRole(long roleId, long criteriaId) {
        roleCriteriaRepository.save(RoleCriteria.builder().roleId(roleId).criteriaId(criteriaId).build());
    }
}
