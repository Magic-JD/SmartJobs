package org.smartjobs.adaptors.data;

import display.CamelCaseDisplayNameGenerator;
import jakarta.persistence.Tuple;
import org.hibernate.sql.results.internal.TupleImpl;
import org.hibernate.sql.results.internal.TupleMetadata;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.data.repository.*;
import org.smartjobs.adaptors.data.repository.data.*;
import org.smartjobs.core.entities.UserScoringCriteria;
import org.smartjobs.core.ports.dal.RoleDal;
import org.smartjobs.core.service.role.data.CriteriaCategory;

import java.util.List;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class RoleDalImplTest {

    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final RoleCriteriaRepository roleCriteriaRepository = mock(RoleCriteriaRepository.class);
    private final UserCriteriaRepository userCriteriaRepository = mock(UserCriteriaRepository.class);
    private final DefinedScoringCriteriaRepository definedScoringCriteriaRepository = mock(DefinedScoringCriteriaRepository.class);
    private final SelectedRoleRepository selectedRoleRepository = mock(SelectedRoleRepository.class);

    private final RoleDal roleDal = new RoleDalImpl(roleRepository, roleCriteriaRepository, userCriteriaRepository, definedScoringCriteriaRepository, selectedRoleRepository);


    private final ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);
    private final ArgumentCaptor<RoleCriteria> roleCriteriaArgumentCaptor = ArgumentCaptor.forClass(RoleCriteria.class);
    private final ArgumentCaptor<SelectedRole> selectedRoleArgumentCaptor = ArgumentCaptor.forClass(SelectedRole.class);
    private final ArgumentCaptor<UserCriteria> userCriteriaArgumentCaptor = ArgumentCaptor.forClass(UserCriteria.class);

    @Test
    void testGetUserRolesReturnsTheCorrectRolesForTheUser() {
        when(roleRepository.findByUserId(USER_ID)).thenReturn(DATABASE_ROLE_LIST);
        assertEquals(ROLE_DISPLAY_LIST, roleDal.getUserRoles(USER_ID));
    }

    @Test
    void testSaveRoleReturnsTheIdOfTheSavedRole() {
        when(roleRepository.saveAndFlush(roleArgumentCaptor.capture())).thenReturn(DATABASE_ROLE);
        long roleId = roleDal.saveRole(USER_ID, POSITION);
        assertEquals(ROLE_ID, roleId);
        Role role = roleArgumentCaptor.getValue();
        assertEquals(USER_ID, role.getUserId());
        assertEquals(POSITION, role.getPosition());
    }

    @Test
    void testGetRoleByIdReturnsTheCorrectRoleById() {
        Tuple tuple1 = new TupleImpl(
                new TupleMetadata(null, new String[]{"ai_prompt", "value", "id", "category", "criteria", "is_boolean", "score", "input"}),
                new Object[]{CRITERIA_REQUEST_SCORE, null, USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS.toString(), CRITERIA_DESCRIPTION, false, (long) MAX_SCORE_VALUE, false}
        );
        Tuple tuple2 = new TupleImpl(
                new TupleMetadata(null, new String[]{"ai_prompt", "value", "id", "category", "criteria", "is_boolean", "score", "input"}),
                new Object[]{CRITERIA_REQUEST_PASS, null, USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS.toString(), CRITERIA_DESCRIPTION, true, (long) MAX_SCORE_VALUE, false}
        );
        when(roleCriteriaRepository.findAllCriteriaByRoleId(ROLE_ID)).thenReturn(List.of(tuple1, tuple2));
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(DATABASE_ROLE));
        assertEquals(ROLE, roleDal.getRoleById(ROLE_ID));
    }

    @Test
    void testGetRoleByIdReturnsTheCorrectRoleByIdWithTheCorrectStringInterpolation() {
        Tuple tuple1 = new TupleImpl(
                new TupleMetadata(null, new String[]{"ai_prompt", "value", "id", "category", "criteria", "is_boolean", "score", "input"}),
                new Object[]{"Check for X", "this", USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS.toString(), "Description of", false, (long) MAX_SCORE_VALUE, true}
        );
        when(roleCriteriaRepository.findAllCriteriaByRoleId(ROLE_ID)).thenReturn(List.of(tuple1));
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(DATABASE_ROLE));
        org.smartjobs.core.entities.Role role = roleDal.getRoleById(ROLE_ID);
        UserScoringCriteria userScoringCriteria = role.userScoringCriteria().getFirst();
        assertEquals("Check for this", userScoringCriteria.scoringGuide());
        assertEquals("Description of: this", userScoringCriteria.criteriaDescription());
    }

    @Test
    void testDeleteRemovesTheRoleFromTheRoleRepository() {
        roleDal.deleteRole(ROLE_ID);
        verify(roleRepository).deleteById(ROLE_ID);
    }

    @Test
    void testRemoveUserCriteriaFromRoleWillDeleteTheUserCriteriaFromTheRole() {
        roleDal.removeUserCriteriaFromRole(ROLE_ID, USER_CRITERIA_ID);
        verify(userCriteriaRepository).deleteById(USER_CRITERIA_ID);
    }

    @Test
    void testSetSelectedRoleWillUpdateTheSelectedRoleForTheUserIfItAlreadyExits() {
        SelectedRole currentRole = new SelectedRole(SELECTED_ROLE_ID, USER_ID, ZERO);
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.of(currentRole));
        roleDal.setSelectedRole(USER_ID, ROLE_ID);
        verify(selectedRoleRepository).saveAndFlush(selectedRoleArgumentCaptor.capture());
        SelectedRole selectedRole = selectedRoleArgumentCaptor.getValue();
        assertEquals(currentRole, selectedRole);
        assertEquals(ROLE_ID, selectedRole.getRoleId());
    }

    @Test
    void testSetSelectedRoleWillCreateNewRoleIfOneDoesNotAlreadyExist() {
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        roleDal.setSelectedRole(USER_ID, ROLE_ID);
        verify(selectedRoleRepository).saveAndFlush(selectedRoleArgumentCaptor.capture());
        SelectedRole selectedRole = selectedRoleArgumentCaptor.getValue();
        assertEquals(ROLE_ID, selectedRole.getRoleId());
        assertEquals(USER_ID, selectedRole.getUserId());
    }

    @Test
    void testGetCurrentlySelectedRoleByUserIdGetsTheCurrentlySelectedRole() {
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.of(SELECTED_ROLE));
        assertEquals(Optional.of(ROLE_ID), roleDal.getCurrentlySelectedRoleByUserId(USER_ID));
    }

    @Test
    void testGetCurrentlySelectedRoleByUserIdReturnsEmptyIfThereIsNoSelectedRole() {
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), roleDal.getCurrentlySelectedRoleByUserId(USER_ID));
    }

    @Test
    void testDeleteCurrentlySelectedRoleDeletesTheCurrentlySelectedRole() {
        roleDal.deleteCurrentlySelectedRole(USER_ID);
        verify(selectedRoleRepository).deleteByUserId(USER_ID);
    }

    @Test
    void testGetCurrentlySelectedRoleReturnsTheCorrectRole() {
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.of(SELECTED_ROLE));
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(DATABASE_ROLE));
        Tuple tuple1 = new TupleImpl(
                new TupleMetadata(null, new String[]{"ai_prompt", "value", "id", "category", "criteria", "is_boolean", "score", "input"}),
                new Object[]{CRITERIA_REQUEST_SCORE, null, USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS.toString(), CRITERIA_DESCRIPTION, false, (long) MAX_SCORE_VALUE, false}
        );
        Tuple tuple2 = new TupleImpl(
                new TupleMetadata(null, new String[]{"ai_prompt", "value", "id", "category", "criteria", "is_boolean", "score", "input"}),
                new Object[]{CRITERIA_REQUEST_PASS, null, USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS.toString(), CRITERIA_DESCRIPTION, true, (long) MAX_SCORE_VALUE, false}
        );
        when(roleCriteriaRepository.findAllCriteriaByRoleId(ROLE_ID)).thenReturn(List.of(tuple1, tuple2));
        assertEquals(Optional.of(ROLE), roleDal.getCurrentlySelectedRole(USER_ID));
    }

    @Test
    void testGetCurrentlySelectedRoleReturnsEmptyIfRoleIsNotFound() {
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), roleDal.getCurrentlySelectedRole(USER_ID));
    }

    @Test
    void testCreateNewUserCriteriaForRoleWillCreateTheCriteriaAndAddTheRoleLink() {
        when(userCriteriaRepository.saveAndFlush(userCriteriaArgumentCaptor.capture())).thenReturn(new UserCriteria(USER_CRITERIA_ID, DEFINED_SCORING_CRITERIA_ID_SCORE, VALUE, MAX_SCORE_VALUE));
        org.smartjobs.core.entities.UserCriteria newUserCriteriaForRole = roleDal.createNewUserCriteriaForRole(DEFINED_SCORING_CRITERIA_ID_SCORE, ROLE_ID, VALUE, MAX_SCORE_VALUE);
        verify(roleCriteriaRepository).save(roleCriteriaArgumentCaptor.capture());

        assertEquals(USER_CRITERIA, newUserCriteriaForRole);

        UserCriteria userCriteria = userCriteriaArgumentCaptor.getValue();
        assertEquals(DEFINED_SCORING_CRITERIA_ID_SCORE, userCriteria.getDefinedCriteriaId());
        assertEquals(MAX_SCORE_VALUE, userCriteria.getScore());
        assertEquals(VALUE, userCriteria.getValue());

        RoleCriteria roleCritera = roleCriteriaArgumentCaptor.getValue();
        assertEquals(USER_CRITERIA_ID, roleCritera.getUserCriteriaId());
        assertEquals(ROLE_ID, roleCritera.getRoleId());
    }

    @Test
    void testCreateNewUserCriteriaForRoleWillCreateTheCriteriaAndAddTheRoleLinkWhenNoValueIsProvided() {
        when(userCriteriaRepository.saveAndFlush(userCriteriaArgumentCaptor.capture())).thenReturn(new UserCriteria(USER_CRITERIA_ID, DEFINED_SCORING_CRITERIA_ID_SCORE, null, MAX_SCORE_VALUE));
        org.smartjobs.core.entities.UserCriteria newUserCriteriaForRole = roleDal.createNewUserCriteriaForRole(DEFINED_SCORING_CRITERIA_ID_SCORE, ROLE_ID, null, MAX_SCORE_VALUE);
        verify(roleCriteriaRepository).save(roleCriteriaArgumentCaptor.capture());

        assertEquals(USER_CRITERIA_WITHOUT_VALUE, newUserCriteriaForRole);

        UserCriteria userCriteria = userCriteriaArgumentCaptor.getValue();
        assertEquals(DEFINED_SCORING_CRITERIA_ID_SCORE, userCriteria.getDefinedCriteriaId());
        assertEquals(MAX_SCORE_VALUE, userCriteria.getScore());
        assertNull(userCriteria.getValue());

        RoleCriteria roleCritera = roleCriteriaArgumentCaptor.getValue();
        assertEquals(USER_CRITERIA_ID, roleCritera.getUserCriteriaId());
        assertEquals(ROLE_ID, roleCritera.getRoleId());
    }

    @Test
    void testCountCriteriaForRoleReturnsTheCriteriaForTheRole() {
        when(roleCriteriaRepository.countByRoleId(ROLE_ID)).thenReturn(ROLE_CRITERIA_COUNT);
        assertEquals(ROLE_CRITERIA_COUNT, roleDal.countCriteriaForRole(ROLE_ID));
    }

    @Test
    void testGetAllDefinedScoringCriteriaReturnsAllTheDefinedScoringCriteria() {
        when(definedScoringCriteriaRepository.findAll()).thenReturn(List.of(
                new DefinedCriteria(DEFINED_SCORING_CRITERIA_ID_SCORE, CriteriaCategory.SOFT_SKILLS.toString(), DEFINED_SCORING_CRITERIA_DESCRIPTION, true, CRITERIA_REQUEST_SCORE, false, INPUT_EXAMPLE, TOOLTIP),
                new DefinedCriteria(DEFINED_SCORING_CRITERIA_ID_PASS, CriteriaCategory.HARD_SKILLS.toString(), DEFINED_SCORING_CRITERIA_DESCRIPTION, false, CRITERIA_REQUEST_PASS, true, null, TOOLTIP)
        ));
        assertEquals(DEFINED_SCORING_CRITERIA_LIST, roleDal.getAllDefinedScoringCriteria());

    }
}
