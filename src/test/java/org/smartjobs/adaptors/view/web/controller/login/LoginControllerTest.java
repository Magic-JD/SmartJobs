package org.smartjobs.adaptors.view.web.controller.login;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.Collections;
import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private final LoginController loginController = new LoginController(USER_SERVICE);

    @Test
    void testShowLoginWillReturnTheLoginPage() {
        ConcurrentModel model = new ConcurrentModel();
        String page = loginController.showLogin(model);
        assertNull(model.getAttribute("error"));
        assertEquals("login/login", page);
    }

    @Test
    void testShowLoginWithErrorWillAddTheErrorAttributeAndThenReturnTheLoginPage() {
        String page = loginController.showLoginWithError(MODEL);
        assertTrue((boolean) MODEL.getAttribute("error"));
        assertEquals("login/login", page);
    }

    @Test
    void testShowRegistrationFormWillShowTheRegistrationPage() {
        String page = loginController.showRegistrationForm(MODEL);
        assertEquals(USER_DTO_EMPTY, MODEL.getAttribute("user"));
        assertEquals(Collections.emptyList(), MODEL.getAttribute("errors"));
        assertEquals("login/register", page);
    }

    @Test
    void testRegisterUserAccountWillRegisterTheUserWithoutAnyIssuesIfThereAreNoProblems() {
        ConcurrentModel model = new ConcurrentModel();
        MockHttpServletResponse response = mockHttpServletResponse();
        String page = loginController.registerUserAccount(model, USER_DTO_NEW, response);
        assertNull(model.getAttribute("errors"));
        assertEquals("/login", response.getHeader("Hx-Redirect"));
        assertEquals("candidate/empty-response", page);
    }

    @Test
    void testRegisterUserAccountWillReturnErrorsIfThereAreErrors() {
        ConcurrentModel model = new ConcurrentModel();
        MockHttpServletResponse response = mockHttpServletResponse();
        String page = loginController.registerUserAccount(model, USER_DTO_EMPTY, response);
        assertEquals(List.of("Password must be more than 8 characters", "Password must not be empty", "Username must not be empty"), model.getAttribute("errors"));
        assertEquals("login/register", page);
    }
}