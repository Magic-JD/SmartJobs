package org.smartjobs.adaptors.view.web.controller.roles;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class RolesControllerTest {

    public static final RolesController ROLES_CONTROLLER = new RolesController(ROLE_SERVICE);
    private final RolesController rolesController = ROLES_CONTROLLER;

    @Test
    void testSavedRolesReturnsAllTheRolesAsWellAsTheCurrentlySelectedOne() {
        String savedRolesFragment = rolesController.savedRoles(USER, MODEL);
        assertEquals(ROLE_DISPLAY_LIST, MODEL.getAttribute("savedRoles"));
        assertEquals(ROLE_ID, MODEL.getAttribute("currentlySelected"));
        assertEquals("role/saved-role", savedRolesFragment);
    }

    @Test
    void testRoleTemplateJustReturnsTheTemplateForTheNewRole() {
        assertEquals("role/role-template", rolesController.roleTemplate());
    }

    @Test
    void testDisplayRoleReturnsTheRoleConvertedToTheCorrectDisplayObject() {
        MockHttpServletResponse response = mockHttpServletResponse();
        String roleTemplate = rolesController.displayRole(USER, ROLE_ID, response, MODEL);
        assertEquals("role-changed", response.getHeader("HX-Trigger"));
        assertEquals(DISPLAY_ROLE, MODEL.getAttribute("role"));
        assertEquals(CATEGORY_STRINGS, MODEL.getAttribute("categories"));
        assertEquals("role/role", roleTemplate);
    }

    @Test
    void testDisplayCurrentlySelectedRoleWhenUserHasSelectedRole() {
        String roleTemplate = rolesController.displayCurrentlySelectedRole(USER, MODEL);
        assertEquals(DISPLAY_ROLE, MODEL.getAttribute("role"));
        assertEquals(CATEGORY_STRINGS, MODEL.getAttribute("categories"));
        assertEquals("role/role", roleTemplate);
    }

    @Test
    void testDoNotDisplayCurrentlySelectedRoleWhenUserHasNoSelectedRole() {
        String roleTemplate = rolesController.displayCurrentlySelectedRole(USER2, MODEL);
        assertEquals("candidate/empty-response", roleTemplate);
    }

    @Test
    void testDeleteRoleDeletesTheRoleAndReturnsAnEmptyFragment() {
        MockHttpServletResponse response = mockHttpServletResponse();
        String roleTemplate = rolesController.deleteRole(USER, ROLE_ID, response);
        assertEquals("role-deleted", response.getHeader("HX-Trigger"));
        assertEquals("candidate/empty-response", roleTemplate);
    }

    @Test
    void testCreateNewRoleShouldCreateAndReturnANewRole() {
        MockHttpServletResponse response = mockHttpServletResponse();
        String roleTemplate = rolesController.createNewRole(USER, POSITION, response, MODEL);
        assertEquals("role-changed", response.getHeader("HX-Trigger"));
        assertEquals(DISPLAY_ROLE_NEW, MODEL.getAttribute("role"));
        assertEquals(CATEGORY_STRINGS, MODEL.getAttribute("categories"));
        assertEquals("role/role", roleTemplate);
    }

    @Test
    void testCriteriaForCategoryReturnsTheCriteriaMatchingTheSelectedCategory() {
        String categoryCriteriaFragment = rolesController.criteriaForCategory("Hard Skills", MODEL);
        assertEquals(List.of(DEFINED_SCORING_CRITERIA_PASS), MODEL.getAttribute("criteria"));
        assertEquals("role/category-criteria", categoryCriteriaFragment);
    }

    @Test
    void testDeleteCriteriaReturnsAnEmptyResponse() {
        String emptyResponse = rolesController.deleteCriteria(USER, USER_CRITERIA_ID);
        assertEquals("candidate/empty-response", emptyResponse);
    }

    @Test
    void testSelectCriteriaWillReturnTheCriteriaThatMatchesTheId() {
        String selectCriteriaFragment = rolesController.selectCriteria(DEFINED_SCORING_CRITERIA_ID_SCORE, MODEL);
        assertEquals(DEFINED_SCORING_CRITERIA_SCORE, MODEL.getAttribute("criteria"));
        assertEquals(INPUT_EXAMPLE, MODEL.getAttribute("placeholderText"));
        assertEquals("role/select-criteria", selectCriteriaFragment);
    }

    @Test
    void testSaveCriteriaSavesTheCriteria() {
        MockHttpServletResponse response = mockHttpServletResponse();
        String roleTemplate = rolesController.saveCriteria(USER, DEFINED_SCORING_CRITERIA_ID_SCORE, VALUE, String.valueOf(MAX_SCORE_VALUE), response);
        assertEquals("role-updated", response.getHeader("HX-Trigger"));
        assertEquals("candidate/empty-response", roleTemplate);
    }
}