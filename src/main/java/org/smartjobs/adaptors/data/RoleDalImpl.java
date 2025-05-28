package org.smartjobs.adaptors.data;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.*;
import org.smartjobs.adaptors.data.repository.data.*;
import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.UserScoringCriteria;
import org.smartjobs.core.exception.categories.ApplicationExceptions.IncorrectIdForRoleRetrievalException;
import org.smartjobs.core.ports.dal.RoleDal;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoleDalImpl implements RoleDal {

    private final RoleRepository roleRepository;
    private final RoleCriteriaRepository roleCriteriaRepository;
    private final UserCriteriaRepository userCriteriaRepository;
    private final DefinedScoringCriteriaRepository definedScoringCriteriaRepository;
    private final SelectedRoleRepository selectedRoleRepository;

    @Autowired
    public RoleDalImpl(RoleRepository roleRepository, RoleCriteriaRepository roleCriteriaRepository, UserCriteriaRepository userCriteriaRepository, DefinedScoringCriteriaRepository definedScoringCriteriaRepository, SelectedRoleRepository selectedRoleRepository) {
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
        return roleRepository.findById(id)
                .map(this::convertToCoreRole)
                .orElseThrow(() -> new IncorrectIdForRoleRetrievalException(id));
    }

    private org.smartjobs.core.entities.Role convertToCoreRole(Role role) {
        return new org.smartjobs.core.entities.Role(
                role.getId(),
                role.getPosition(),
                role.getRoleCriteria().stream().map(this::convertRoleCriteria).toList());
    }

    private UserScoringCriteria convertRoleCriteria(RoleCriteria rc) {
        UserCriteria userCriteria = rc.getUserCriteria();
        DefinedCriteria definedCriteria = userCriteria.getDefinedCriteria();
        String aiPrompt = definedCriteria.getAiPrompt();
        String value = userCriteria.getValue();
        if(value != null){
            aiPrompt = aiPrompt.replace("X", value);
        }
        return new UserScoringCriteria(
                rc.getId(),
                CriteriaCategory.getFromName(definedCriteria.getCategory()),
                definedCriteria.getCriteria() + (definedCriteria.isInput() ? STR.": \{value}" : ""),
                definedCriteria.isBoolean(),
                userCriteria.getScore(),
                aiPrompt);
    }

    @Override
    public void deleteRole(long roleId) {
        roleRepository.deleteById(roleId);
    }


    @Override
    public void removeUserCriteriaFromRole(long userCriteriaId) {
        userCriteriaRepository.deleteById(userCriteriaId); //Handled through cascade
    }

    @Override
    @Transactional
    public void setSelectedRole(long userId, long roleId) {
        Role roleRef = roleRepository.getReferenceById(roleId);
        selectedRoleRepository.saveAndFlush(selectedRoleRepository.findByUserId(userId)
                .map(sr -> {
                    sr.setRole(roleRef);
                    return sr;
                })
                .orElse(SelectedRole.builder().userId(userId).role(roleRef).build()));
    }

    @Override
    public Optional<Long> getCurrentlySelectedRoleByUserId(long userId) {
        return selectedRoleRepository.findRoleIdByUserId(userId);
    }

    @Override
    public void deleteCurrentlySelectedRole(long userId) {
        selectedRoleRepository.deleteByUserId(userId);
    }

    @Override
    public Optional<org.smartjobs.core.entities.Role> getCurrentlySelectedRole(long userId) {
        return selectedRoleRepository.findByUserId(userId)
                .map(SelectedRole::getRole)
                .map(this::convertToCoreRole);
    }

    @Transactional
    public org.smartjobs.core.entities.UserCriteria createNewUserCriteriaForRole(long definedCriteriaId, long roleId, String value, int score) {
        DefinedCriteria definedCriteria = definedScoringCriteriaRepository.getReferenceById(definedCriteriaId);
        Role role = roleRepository.getReferenceById(roleId);
        UserCriteria userCriteria = userCriteriaRepository.saveAndFlush(UserCriteria.builder().definedCriteria(definedCriteria).value(value).score(score).build());
        roleCriteriaRepository.save(RoleCriteria.builder().role(role).userCriteria(userCriteria).build());
        return new org.smartjobs.core.entities.UserCriteria(userCriteria.getId(), userCriteria.getDefinedCriteria().getId(), Optional.ofNullable(userCriteria.getValue()), userCriteria.getScore());
    }

    @Override
    public int countCriteriaForRole(long roleId) {
        return roleCriteriaRepository.countByRoleId(roleId);
    }

    @Override
    public List<DefinedScoringCriteria> getAllDefinedScoringCriteria() {
        return definedScoringCriteriaRepository.findAll().stream()
                .map(dsc -> new DefinedScoringCriteria(dsc.getId(),
                        dsc.getCriteria(),
                        CriteriaCategory.getFromName(dsc.getCategory()),
                        dsc.isInput(),
                        Optional.ofNullable(dsc.getInputExample()),
                        dsc.getAiPrompt(),
                        dsc.getTooltip()))
                .toList();
    }
}

