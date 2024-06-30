package org.smartjobs.core.service;

import jakarta.transaction.Transactional;
import org.smartjobs.core.exception.categories.UserResolvedExceptions;
import org.smartjobs.core.service.user.validation.UserDto;

import java.util.List;

public interface UserRegistration {

    @Transactional
    List<String> registerNewUserAccount(UserDto userDto) throws UserResolvedExceptions.UserAlreadyExistsException;
}
