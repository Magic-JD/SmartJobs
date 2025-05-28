package org.smartjobs.adaptors.data;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.BeforeEach;
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

    private RoleRepository roleRepository = mock(RoleRepository.class);
    private RoleCriteriaRepository roleCriteriaRepository = mock(RoleCriteriaRepository.class);
    private UserCriteriaRepository userCriteriaRepository = mock(UserCriteriaRepository.class);
    private DefinedScoringCriteriaRepository definedScoringCriteriaRepository = mock(DefinedScoringCriteriaRepository.class);
    private SelectedRoleRepository selectedRoleRepository = mock(SelectedRoleRepository.class);

    private RoleDal roleDal = new RoleDalImpl(roleRepository, roleCriteriaRepository, userCriteriaRepository, definedScoringCriteriaRepository, selectedRoleRepository);


    private final ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);
    private final ArgumentCaptor<RoleCriteria> roleCriteriaArgumentCaptor = ArgumentCaptor.forClass(RoleCriteria.class);
    private final ArgumentCaptor<SelectedRole> selectedRoleArgumentCaptor = ArgumentCaptor.forClass(SelectedRole.class);
    private final ArgumentCaptor<UserCriteria> userCriteriaArgumentCaptor = ArgumentCaptor.forClass(UserCriteria.class);

    @BeforeEach
    void before(){
        roleRepository = mock(RoleRepository.class);
        roleCriteriaRepository = mock(RoleCriteriaRepository.class);
        userCriteriaRepository = mock(UserCriteriaRepository.class);
        definedScoringCriteriaRepository = mock(DefinedScoringCriteriaRepository.class);
        selectedRoleRepository = mock(SelectedRoleRepository.class);
        roleDal = new RoleDalImpl(roleRepository, roleCriteriaRepository, userCriteriaRepository, definedScoringCriteriaRepository, selectedRoleRepository);
    }

    @Test
    void testGetUserRolesReturnsTheCorrectRolesForTheUser() {
        doReturn(DATABASE_ROLE_LIST).when(roleRepository).findByUserId(USER_ID);
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
        doReturn(Optional.of(DATABASE_ROLE)).when(roleRepository).findById(ROLE_ID);
        assertEquals(ROLE, roleDal.getRoleById(ROLE_ID));
    }

    @Test
    void testGetRoleByIdReturnsTheCorrectRoleByIdWithTheCorrectStringInterpolation() {
        doReturn(Optional.of(DATABASE_ROLE)).when(roleRepository).findById(ROLE_ID);
        org.smartjobs.core.entities.Role role = roleDal.getRoleById(ROLE_ID);
        UserScoringCriteria userScoringCriteria = role.userScoringCriteria().stream().filter(uc -> uc.id() == USER_CRITERIA_ID2).findAny().orElseThrow();
        assertEquals(CRITERIA_REQUEST_SCORE, userScoringCriteria.scoringGuide());
        assertEquals(CRITERIA_DESCRIPTION, userScoringCriteria.criteriaDescription());
    }

    @Test
    void testDeleteRemovesTheRoleFromTheRoleRepository() {
        roleDal.deleteRole(ROLE_ID);
        verify(roleRepository).deleteById(ROLE_ID);
    }

    @Test
    void testRemoveUserCriteriaFromRoleWillDeleteTheUserCriteriaFromTheRole() {
        roleDal.removeUserCriteriaFromRole(USER_CRITERIA_ID);
        verify(userCriteriaRepository).deleteById(USER_CRITERIA_ID);
    }

    @Test
    void testSetSelectedRoleWillUpdateTheSelectedRoleForTheUserIfItAlreadyExits() {
        SelectedRole currentRole = new SelectedRole(SELECTED_ROLE_ID, USER_ID, DATABASE_ROLE);
        doReturn(DATABASE_ROLE).when(roleRepository).getReferenceById(ROLE_ID);
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.of(currentRole));
        roleDal.setSelectedRole(USER_ID, ROLE_ID);
        verify(selectedRoleRepository).saveAndFlush(selectedRoleArgumentCaptor.capture());
        SelectedRole selectedRole = selectedRoleArgumentCaptor.getValue();
        assertEquals(currentRole, selectedRole);
        assertEquals(ROLE_ID, selectedRole.getRole().getId());
    }

    @Test
    void testSetSelectedRoleWillCreateNewRoleIfOneDoesNotAlreadyExist() {
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        doReturn(DATABASE_ROLE).when(roleRepository).getReferenceById(ROLE_ID);
        roleDal.setSelectedRole(USER_ID, ROLE_ID);
        verify(selectedRoleRepository).saveAndFlush(selectedRoleArgumentCaptor.capture());
        SelectedRole selectedRole = selectedRoleArgumentCaptor.getValue();
        assertEquals(ROLE_ID, selectedRole.getRole().getId());
        assertEquals(USER_ID, selectedRole.getUserId());
    }

    @Test
    void testGetCurrentlySelectedRoleByUserIdGetsTheCurrentlySelectedRole() {
        when(selectedRoleRepository.findRoleIdByUserId(USER_ID)).thenReturn(Optional.of(ROLE_ID));
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
        doReturn(Optional.of(SELECTED_ROLE)).when(selectedRoleRepository).findByUserId(USER_ID);
        doReturn(Optional.of(DATABASE_ROLE)).when(roleRepository).findById(ROLE_ID);
        assertEquals(Optional.of(ROLE), roleDal.getCurrentlySelectedRole(USER_ID));
    }

    @Test
    void testGetCurrentlySelectedRoleReturnsEmptyIfRoleIsNotFound() {
        when(selectedRoleRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), roleDal.getCurrentlySelectedRole(USER_ID));
    }

    @Test
    void testCreateNewUserCriteriaForRoleWillCreateTheCriteriaAndAddTheRoleLink() {
        doReturn(new UserCriteria(USER_CRITERIA_ID, DEFINED_CRITERIA_SCORE, VALUE, MAX_SCORE_VALUE)).when(userCriteriaRepository).saveAndFlush(userCriteriaArgumentCaptor.capture());
        doReturn(DEFINED_CRITERIA_SCORE).when(definedScoringCriteriaRepository).getReferenceById(DEFINED_SCORING_CRITERIA_ID_SCORE);
        doReturn(DATABASE_ROLE).when(roleRepository).getReferenceById(ROLE_ID);
        org.smartjobs.core.entities.UserCriteria newUserCriteriaForRole = roleDal.createNewUserCriteriaForRole(DEFINED_SCORING_CRITERIA_ID_SCORE, ROLE_ID, VALUE, MAX_SCORE_VALUE);
        verify(roleCriteriaRepository).save(roleCriteriaArgumentCaptor.capture());

        assertEquals(USER_CRITERIA, newUserCriteriaForRole);

        UserCriteria userCriteria = userCriteriaArgumentCaptor.getValue();
        assertEquals(DEFINED_SCORING_CRITERIA_ID_SCORE, userCriteria.getDefinedCriteria().getId());
        assertEquals(MAX_SCORE_VALUE, userCriteria.getScore());
        assertEquals(VALUE, userCriteria.getValue());

        RoleCriteria roleCritera = roleCriteriaArgumentCaptor.getValue();
        assertEquals(USER_CRITERIA_ID, roleCritera.getUserCriteria().getId());
        assertEquals(ROLE_ID, roleCritera.getRole().getId());
    }

    @Test
    void testCreateNewUserCriteriaForRoleWillCreateTheCriteriaAndAddTheRoleLinkWhenNoValueIsProvided() {
        doReturn(new UserCriteria(USER_CRITERIA_ID, DEFINED_CRITERIA_SCORE, null, MAX_SCORE_VALUE)).when(userCriteriaRepository).saveAndFlush(userCriteriaArgumentCaptor.capture());
        doReturn(DEFINED_CRITERIA_SCORE).when(definedScoringCriteriaRepository).getReferenceById(DEFINED_SCORING_CRITERIA_ID_SCORE);
        doReturn(DATABASE_ROLE).when(roleRepository).getReferenceById(ROLE_ID);
        org.smartjobs.core.entities.UserCriteria newUserCriteriaForRole = roleDal.createNewUserCriteriaForRole(DEFINED_SCORING_CRITERIA_ID_SCORE, ROLE_ID, null, MAX_SCORE_VALUE);
        verify(roleCriteriaRepository).save(roleCriteriaArgumentCaptor.capture());

        assertEquals(USER_CRITERIA_WITHOUT_VALUE, newUserCriteriaForRole);

        UserCriteria userCriteria = userCriteriaArgumentCaptor.getValue();
        assertEquals(DEFINED_SCORING_CRITERIA_ID_SCORE, userCriteria.getDefinedCriteria().getId());
        assertEquals(MAX_SCORE_VALUE, userCriteria.getScore());
        assertNull(userCriteria.getValue());

        RoleCriteria roleCritera = roleCriteriaArgumentCaptor.getValue();
        assertEquals(USER_CRITERIA_ID, roleCritera.getUserCriteria().getId());
        assertEquals(ROLE_ID, roleCritera.getRole().getId());
    }

    @Test
    void testCountCriteriaForRoleReturnsTheCriteriaForTheRole() {
        when(roleCriteriaRepository.countByRoleId(ROLE_ID)).thenReturn(ROLE_CRITERIA_COUNT);
        assertEquals(ROLE_CRITERIA_COUNT, roleDal.countCriteriaForRole(ROLE_ID));
    }

    @Test
    void testGetAllDefinedScoringCriteriaReturnsAllTheDefinedScoringCriteria() {
        doReturn(List.of(
                new DefinedCriteria(DEFINED_SCORING_CRITERIA_ID_SCORE, CriteriaCategory.SOFT_SKILLS.toString(), DEFINED_SCORING_CRITERIA_DESCRIPTION, true, CRITERIA_REQUEST_SCORE, false, INPUT_EXAMPLE, TOOLTIP),
                new DefinedCriteria(DEFINED_SCORING_CRITERIA_ID_PASS, CriteriaCategory.HARD_SKILLS.toString(), DEFINED_SCORING_CRITERIA_DESCRIPTION, false, CRITERIA_REQUEST_PASS, true, null, TOOLTIP)
        )).when(definedScoringCriteriaRepository).findAll();
        assertEquals(DEFINED_SCORING_CRITERIA_LIST, roleDal.getAllDefinedScoringCriteria());

    }
}
