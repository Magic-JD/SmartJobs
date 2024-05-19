package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.*;
import org.smartjobs.adaptors.data.repository.data.*;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.ScoringCriteria;
import org.smartjobs.core.exception.categories.ApplicationExceptions.IncorrectIdForRoleRetrievalException;
import org.smartjobs.core.ports.dal.RoleDao;
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
    private final SelectedRoleRepository selectedRoleRepository;

    @Autowired
    public RoleDaoImpl(RoleRepository roleRepository, RoleCriteriaRepository roleCriteriaRepository, UserCriteriaRepository userCriteriaRepository, DefinedScoringCriteriaRepository definedScoringCriteriaRepository, SelectedRoleRepository selectedRoleRepository) {
        this.roleRepository = roleRepository;
        this.roleCriteriaRepository = roleCriteriaRepository;
        this.userCriteriaRepository = userCriteriaRepository;
        this.definedScoringCriteriaRepository = definedScoringCriteriaRepository;
        this.selectedRoleRepository = selectedRoleRepository;
    }

    @Override
    public List<RoleDisplay> getUserRoles(long userId) {
        return roleRepository.findByUserId(userId).stream()
                .map(role -> new RoleDisplay(role.getId(), role.getPosition()))
                .toList();
    }

    @Override
    public long saveRole(long userId, String name) {
        return roleRepository.saveAndFlush(Role.builder().userId(userId).position(name).build()).getId();
    }

    @Override
    public org.smartjobs.core.entities.Role getRoleById(long id) {
        var criteria = roleCriteriaRepository.findAllByRoleId(id).stream()
                .map(rc -> userCriteriaRepository.findById(rc.getCriteriaId())).map(Optional::orElseThrow).map(userCriteria -> {
                    DefinedCriteria defCrit = definedScoringCriteriaRepository.findById(userCriteria.getDefinedCriteriaId()).orElseThrow();
                    String aiPrompt = defCrit.getAiPrompt();
                    return new ScoringCriteria(
                            userCriteria.getId(),
                            CriteriaCategory.getFromName(defCrit.getCategory()),
                            defCrit.getCriteria() + (defCrit.isInput() ? ": " + userCriteria.getValue() : ""),
                            defCrit.isBoolean(),
                            userCriteria.getScore(),
                            userCriteria.getValue() == null ? aiPrompt : aiPrompt.replace("X", userCriteria.getValue()));
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

    @Override
    public void setSelectedRole(long userId, long roleId) {
        selectedRoleRepository.saveAndFlush(selectedRoleRepository.findByUsername(userId)
                .map(sr -> {
                    sr.setRoleId(roleId);
                    return sr;
                })
                .orElse(SelectedRole.builder().userId(userId).roleId(roleId).build()));
    }

    @Override
    public Optional<Long> getCurrentlySelectedRoleById(long userId) {
        return selectedRoleRepository.findByUsername(userId).map(SelectedRole::getRoleId);
    }

    @Override
    public void deleteCurrentlySelectedRole(long userId) {
        selectedRoleRepository.deleteByUserId(userId);
    }

    @Override
    public Optional<org.smartjobs.core.entities.Role> getCurrentlySelectedRole(long userId) {
        return selectedRoleRepository.findByUsername(userId)
                .map(SelectedRole::getRoleId)
                .map(this::getRoleById);
    }

    @Override
    public org.smartjobs.core.entities.UserCriteria createNewUserCriteria(long definedCriteriaId, String value, int score) {
        UserCriteria userCriteria = userCriteriaRepository.saveAndFlush(UserCriteria.builder().definedCriteriaId(definedCriteriaId).value(value).score(score).build());
        return new org.smartjobs.core.entities.UserCriteria(userCriteria.getId(), userCriteria.getDefinedCriteriaId(), Optional.ofNullable(userCriteria.getValue()), userCriteria.getScore());
    }

    @Override
    public void deleteUserCriteria(long criteriaId) {
        userCriteriaRepository.deleteById(criteriaId);
    }
}

