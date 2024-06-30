package org.smartjobs.core.service.user;

import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.ports.dal.CredentialDal;
import org.smartjobs.core.service.user.validation.UserDto;

import java.util.Collections;
import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        List<String> errors = userService.registerNewUserAccount(new UserDto("new username", PASSWORD2, PASSWORD2));
        assertEquals(Collections.emptyList(), errors);
    }

    @Test
    void testRegisterNewUserAccountWillReturnAnErrorWhenTheAccountAlreadyExists() {
        List<String> errors = userService.registerNewUserAccount(new UserDto(USERNAME, PASSWORD2, PASSWORD2));
        assertEquals(List.of("An account for that username/email already exists"), errors);
    }

    @Test
    void testRegisterNewUserAccountWillIgnoreCaseWhenCheckingItForDuplicates() {
        List<String> errors = userService.registerNewUserAccount(new UserDto("USERNAME", PASSWORD2, PASSWORD2));
        assertEquals(List.of("An account for that username/email already exists"), errors);
    }

    @Test
    void testRegisterNewUserAccountWillIgnoreCaseWhenCreatingIt() {
        CredentialDal credentialDal = credentialDalMock();
        UserService userService = new UserService(credentialDal, passwordEncoder(), validator(), EVENT_EMITTER, SECURE_RANDOM);
        userService.registerNewUserAccount(new UserDto("NEW USERNAME", PASSWORD2, PASSWORD2));
        verify(credentialDal).setUser("new username", PASSWORD2);
    }

    @Test
    void testRegisterNewUserAccountWillReturnErrorsWhenTheDtoIsNotCorrectlyConstructed() {
        List<String> errors = userService.registerNewUserAccount(new UserDto("", "", "pw"));
        assertEquals(List.of(
                "Password must be more than 8 characters",
                "Password must not be empty",
                "Passwords don't match",
                "Username must not be empty"), errors);
        errors = userService.registerNewUserAccount(new UserDto(null, null, "pw"));
        assertEquals(List.of(
                "Password must not be empty",
                "Password must not be null",
                "Passwords don't match",
                "Username must not be empty",
                "Username must not be null"
        ), errors);
        errors = userService.registerNewUserAccount(new UserDto("Username", "password", "password"));
        assertEquals(List.of(
                "Password must be more than 8 characters"
        ), errors);
    }
}