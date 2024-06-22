package org.smartjobs.adaptors.data;

import jakarta.transaction.Transactional;
import org.smartjobs.adaptors.data.repository.*;
import org.smartjobs.adaptors.data.repository.data.Role;
import org.smartjobs.adaptors.data.repository.data.RoleCriteria;
import org.smartjobs.adaptors.data.repository.data.SelectedRole;
import org.smartjobs.adaptors.data.repository.data.UserCriteria;
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

import static java.util.concurrent.CompletableFuture.supplyAsync;

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
        var criteriaF = supplyAsync(() -> roleCriteriaRepository.findAllCriteriaByRoleId(id).stream()
                .map(tuple -> {
                    String aiPrompt = tuple.get("ai_prompt", String.class);
                    String value = tuple.get("value", String.class);
                    return new UserScoringCriteria(
                            tuple.get("id", Long.class),
                            CriteriaCategory.getFromName(tuple.get("category", String.class)),
                            tuple.get("criteria", String.class) + (tuple.get("input", Boolean.class) ? ": " + value : ""),
                            tuple.get("is_boolean", Boolean.class),
                            tuple.get("score", Long.class),
                            value == null ? aiPrompt : aiPrompt.replace("X", value));
                }).toList());
        var roleFoundByIdF = supplyAsync(() -> roleRepository.findById(id));
        var criteria = criteriaF.join();
        var roleFoundById = roleFoundByIdF.join();
        return roleFoundById
                .map(role -> new org.smartjobs.core.entities.Role(role.getId(), role.getPosition(), criteria))
                .orElseThrow(() -> new IncorrectIdForRoleRetrievalException(id));
    }

    @Override
    public void deleteRole(long roleId) {
        roleRepository.deleteById(roleId);
    }


    @Override
    public void removeUserCriteriaFromRole(long roleId, long userCriteriaId) {
        userCriteriaRepository.deleteById(userCriteriaId); //Handled through cascade
    }

    @Override
    public void setSelectedRole(long userId, long roleId) {
        selectedRoleRepository.saveAndFlush(selectedRoleRepository.findByUserId(userId)
                .map(sr -> {
                    sr.setRoleId(roleId);
                    return sr;
                })
                .orElse(SelectedRole.builder().userId(userId).roleId(roleId).build()));
    }

    @Override
    public Optional<Long> getCurrentlySelectedRoleByUserId(long userId) {
        return selectedRoleRepository.findByUserId(userId).map(SelectedRole::getRoleId);
    }

    @Override
    public void deleteCurrentlySelectedRole(long userId) {
        selectedRoleRepository.deleteByUserId(userId);
    }

    @Override
    public Optional<org.smartjobs.core.entities.Role> getCurrentlySelectedRole(long userId) {
        return selectedRoleRepository.findByUserId(userId)
                .map(SelectedRole::getRoleId)
                .map(this::getRoleById);
    }

    @Transactional
    public org.smartjobs.core.entities.UserCriteria createNewUserCriteriaForRole(long definedCriteriaId, long roleId, String value, int score) {
        UserCriteria userCriteria = userCriteriaRepository.saveAndFlush(UserCriteria.builder().definedCriteriaId(definedCriteriaId).value(value).score(score).build());
        roleCriteriaRepository.save(RoleCriteria.builder().roleId(roleId).userCriteriaId(userCriteria.getId()).build());
        return new org.smartjobs.core.entities.UserCriteria(userCriteria.getId(), userCriteria.getDefinedCriteriaId(), Optional.ofNullable(userCriteria.getValue()), userCriteria.getScore());
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

