package org.smartjobs.core.service;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.RoleDisplay;
import org.smartjobs.core.entities.UserCriteria;
import org.smartjobs.core.ports.dal.RoleDal;
import org.smartjobs.core.service.role.RoleServiceImpl;
import org.smartjobs.core.service.role.data.CriteriaCategory;

import java.util.List;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class RoleServiceTest {

    public static final int MAX_ALLOWED_CRITERIA = 10;
    public static final String SCORE_STRING = "10";
    private RoleService roleService;
    private RoleDal roleDal;

    @BeforeEach
    void setUp() {
        roleDal = roleDalMock();
        roleService = new RoleServiceImpl(roleDal, MAX_ALLOWED_CRITERIA);
    }

    @Test
    void testGetCurrentlySelectedRoleIdReturnsTheExpectedRoleId() {
        Optional<Long> currentlySelectedRoleId = roleService.getCurrentlySelectedRoleId(USER_ID);
        assertEquals(Optional.of(ROLE_ID), currentlySelectedRoleId);
    }

    @Test
    void testGetCurrentlySelectedRoleReturnsTheExpectedRole() {
        Optional<Role> currentlySelectedRole = roleService.getCurrentlySelectedRole(USER_ID);
        assertEquals(Optional.of(ROLE), currentlySelectedRole);
    }

    @Test
    void testDeleteCurrentlySelectedRoleDeletesTheCurrentlySelectedRoleFromThePersistenceLayer() {
        roleService.deleteCurrentlySelectedRole(USER_ID);
        verify(roleDal).deleteCurrentlySelectedRole(USER_ID);
    }

    @Test
    void testSetCurrentlySelectedRoleSetsTheGivenRole() {
        roleService.setCurrentlySelectedRole(USER_ID, ROLE_ID);
        verify(roleDal).setSelectedRole(USER_ID, ROLE_ID);
    }

    @Test
    void testGetRoleReturnsTheGivenRole() {
        Role role = roleService.getRole(ROLE_ID);
        assertEquals(ROLE, role);
    }

    @Test
    void testGetUserRolesGetsTheCorrectRolesForTheUser() {
        List<RoleDisplay> userRoles = roleService.getUserRoles(USER_ID);
        assertEquals(ROLE_DISPLAY_LIST, userRoles);
    }

    @Test
    void testCreateRoleCreatesNewRole() {
        Role role = roleService.createRole(POSITION, USER_ID);
        assertEquals(ROLE_NEW, role);
    }

    @Test
    void testDeleteRoleDeletesTheRoleFromThePersistenceLayer() {
        roleService.deleteRole(USER_ID, ROLE_ID);
        verify(roleDal).deleteRole(ROLE_ID);
    }

    @Test
    void testRemoveCriteriaFromRoleWillRemoveTheCriteriaFromTheRole() {
        roleService.removeCriteriaFromRole(USER_ID, ROLE_ID, USER_CRITERIA_ID);
        verify(roleDal).removeUserCriteriaFromRole(ROLE_ID, USER_CRITERIA_ID);
    }

    @Test
    void testGetScoringCriteriaForCategoryWillReturnOnlyTheScoringCriteriaForThatCategory() {
        List<DefinedScoringCriteria> scoringCriteriaForCategory = roleService.getScoringCriteriaForCategory(CriteriaCategory.HARD_SKILLS);
        assertEquals(List.of(DEFINED_SCORING_CRITERIA_PASS), scoringCriteriaForCategory);
    }

    @Test
    void testGetCriteriaByIdWillGetTheCriteriaForThatId() {
        DefinedScoringCriteria criteriaById = roleService.getCriteriaById(DEFINED_SCORING_CRITERIA_ID_SCORE);
        assertEquals(DEFINED_SCORING_CRITERIA_SCORE, criteriaById);
        criteriaById = roleService.getCriteriaById(DEFINED_SCORING_CRITERIA_ID_PASS);
        assertEquals(DEFINED_SCORING_CRITERIA_PASS, criteriaById);
    }

    @Test
    void testAddUserCriteriaToRoleWillAddTheUserCriteriaToTheRole() {
        UserCriteria userCriteria = roleService.addUserCriteriaToRole(DEFINED_SCORING_CRITERIA_ID_SCORE, USER_ID, ROLE_ID, VALUE, SCORE_STRING);
        assertEquals(USER_CRITERIA, userCriteria);
    }
}
