package org.smartjobs.adaptors.view.web.controller;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.adaptors.view.web.entities.NavElement;
import org.smartjobs.core.config.ApplicationConfig;

import java.util.Collections;
import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class PageControllerTest {

    public static final NavElement ROLES_NAV_ELEMENT = new NavElement("roles", "Roles", false);
    public static final NavElement CANDIDATES_NAV_ELEMENT = new NavElement("candidates", "Candidates", false);
    public static final List<NavElement> FULL_NAV_ELEMENTS = List.of(ROLES_NAV_ELEMENT, CANDIDATES_NAV_ELEMENT);
    public static final List<NavElement> EMPTY_NAV_LIST = Collections.emptyList();
    PageController pageController = new PageController(CREDIT_SERVICE, ROLE_SERVICE, CANDIDATE_SERVICE, new ApplicationConfig().decimalFormat());

    @Test
    void testOverviewShowsAllAvailableNavOptionsForASignedInUser() {
        String overview = pageController.overview(USER, MODEL);
        assertEquals(String.valueOf(CREDIT_AMOUNT), MODEL.getAttribute("credits"));
        assertEquals(USERNAME, MODEL.getAttribute("username"));
        assertEquals(true, MODEL.getAttribute("loggedIn"));
        assertEquals(FULL_NAV_ELEMENTS, MODEL.getAttribute("navElements"));
        assertEquals("index", overview);
    }

    @Test
    void testOverviewShowsNoAdditionalNavElementsForNonSignedInUser() {
        String overview = pageController.overview(null, MODEL);
        assertEquals(false, MODEL.getAttribute("loggedIn"));
        assertEquals(EMPTY_NAV_LIST, MODEL.getAttribute("navElements"));
        assertEquals("index", overview);
    }

    @Test
    void testGetAnalysisPageAddsTheInfoBoxInfo() {
        String analysisPage = pageController.getAnalysisPage(USER, MODEL);
        assertEquals(SELECTED_CANDIDATE_COUNT, MODEL.getAttribute("selectedCount"));
        assertEquals(POSITION, MODEL.getAttribute("currentRole"));
        assertEquals("analyze/analyze", analysisPage);
    }

    @Test
    void testGetCandidatesPageAddsTheInfoBoxInfo() {
        String analysisPage = pageController.getCandidatesPage(USER, MODEL);
        assertEquals(SELECTED_CANDIDATE_COUNT, MODEL.getAttribute("selectedCount"));
        assertEquals(POSITION, MODEL.getAttribute("currentRole"));
        assertEquals("candidate/candidates", analysisPage);
    }

    @Test
    void testGetUploadPageReturnsTheCorrectString() {
        String uploadPage = pageController.getUploadPage(USER, MODEL);
        assertEquals("upload/upload", uploadPage);
    }

    @Test
    void testGetRolesPage() {
        String rolesPage = pageController.getRolesPage(USER, mockHttpServletResponse(), MODEL);
        assertEquals(ROLE_DISPLAY_LIST, MODEL.getAttribute("savedRoles"));
        assertEquals(ROLE_ID, MODEL.getAttribute("currentlySelected"));
        assertEquals(DISPLAY_ROLE, MODEL.getAttribute("role"));
        assertEquals(CATEGORY_STRINGS, MODEL.getAttribute("categories"));
        assertEquals("role/roles", rolesPage);
    }
}