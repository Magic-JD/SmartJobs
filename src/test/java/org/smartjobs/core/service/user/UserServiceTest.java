package org.smartjobs.core.service.user;

import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.ValidateEmailEvent;
import org.smartjobs.core.service.user.validation.UserDto;

import java.util.Collections;
import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserServiceTest {

    private final UserService userService = USER_SERVICE;

    @Test
    void testLoadUserByUsernameWillReturnTheFoundUser() {
        User user = userService.loadUserByUsername(USERNAME);
        assertEquals(USER, user);
    }

    @Test
    void testRegisterNewUserAccountWillReturnAnEmptyListWithoutAnyError() {
        List<String> errors = userService.validateUser(new UserDto("new@email.com", PASSWORD2, PASSWORD2));
        assertEquals(Collections.emptyList(), errors);
    }

    @Test
    void testRegisterNewUserAccountWillReturnAnErrorWhenTheAccountAlreadyExists() {
        List<String> errors = userService.validateUser(new UserDto(USERNAME, PASSWORD2, PASSWORD2));
        assertEquals(List.of("An account for that email already exists"), errors);
    }

    @Test
    void testRegisterNewUserAccountWillIgnoreCaseWhenCheckingItForDuplicates() {
        List<String> errors = userService.validateUser(new UserDto(USERNAME, PASSWORD2, PASSWORD2));
        assertEquals(List.of("An account for that email already exists"), errors);
    }

    @Test
    void testRegisterNewUserAccountWillIgnoreCaseWhenCreatingIt() {
        EventEmitter eventEmitter = mock(EventEmitter.class);
        UserService userService = new UserService(CREDENTIAL_DAL, PASSWORD_ENCODER, VALIDATOR, eventEmitter, CODE_SUPPLIER, EMAIL_VALIDATION_CACHE);
        userService.validateUser(new UserDto(USERNAME3, PASSWORD2, PASSWORD2));
        verify(eventEmitter).sendEvent(new ValidateEmailEvent(USERNAME3, CODE));
    }

    @Test
    void testRegisterNewUserAccountWillReturnErrorsWhenTheDtoIsNotCorrectlyConstructed() {
        List<String> errors = userService.validateUser(new UserDto("", "", "pw"));
        assertEquals(List.of(
                "Email must be a well-formed email address",
                "Email must not be empty",
                "Password must be more than 8 characters",
                "Password must not be empty",
                "Passwords don't match"
        ), errors);
        errors = userService.validateUser(new UserDto(null, null, "pw"));
        assertEquals(List.of(
                "Email must not be empty",
                "Email must not be null",
                "Password must not be empty",
                "Password must not be null",
                "Passwords don't match"
        ), errors);
        errors = userService.validateUser(new UserDto(USERNAME3, "password", "password"));
        assertEquals(List.of(
                "Password must be more than 8 characters"
        ), errors);
    }
}