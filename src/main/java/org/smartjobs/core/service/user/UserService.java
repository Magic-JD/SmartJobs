package org.smartjobs.core.service.user;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.UserAlreadyExistsException;
import org.smartjobs.core.ports.dal.CredentialDal;
import org.smartjobs.core.service.UserRegistration;
import org.smartjobs.core.service.user.validation.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService, UserRegistration {

    private final CredentialDal credentialDal;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @Autowired
    public UserService(CredentialDal credentialDal, PasswordEncoder passwordEncoder, Validator validator) {
        this.credentialDal = credentialDal;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialDal.getUser(username).orElseThrow(() -> new UsernameNotFoundException("The user could not be found"));
    }

    @Override
    @Transactional
    public List<String> registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException {
        var errors = validator.validate(userDto).stream()
                .map(this::convertConstraintToMessage)
                .sorted()
                .toList();
        if (!errors.isEmpty()) {
            return errors;
        }
        String username = userDto.username().toLowerCase();
        if (userExists(username)) {
            return List.of("An account for that username/email already exists");
        }
        String password = passwordEncoder.encode(userDto.password());
        credentialDal.setUser(username, password);
        return Collections.emptyList();
    }

    private boolean userExists(String email) {
        return credentialDal.getUser(email).isPresent();
    }

    private String convertConstraintToMessage(ConstraintViolation<UserDto> constraint) {
        return STR. "\{ StringUtils.capitalize(constraint.getPropertyPath().toString()) } \{ constraint.getMessage() }" .trim();
    }
}
