package org.smartjobs.core.service.user;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.SendEmailEvent;
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

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService, UserRegistration {

    private final CredentialDal credentialDal;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final EventEmitter eventEmitter;
    private final SecureRandom random;

    @Autowired
    public UserService(CredentialDal credentialDal, PasswordEncoder passwordEncoder, Validator validator, EventEmitter eventEmitter, SecureRandom random) {
        this.credentialDal = credentialDal;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.eventEmitter = eventEmitter;
        this.random = random;
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
        eventEmitter.sendEvent(new SendEmailEvent(username, generateRandomIdentifier()));
        return Collections.emptyList();
    }

    private String generateRandomIdentifier() {
        StringBuilder number = new StringBuilder(String.valueOf(random.nextInt(1_000_000)));
        int zerosNeeded = 6 - number.length();
        for (int i = 0; i < zerosNeeded; i++) {
            number.insert(0, "0");
        }
        return number.toString();
    }

    private boolean userExists(String email) {
        return credentialDal.getUser(email).isPresent();
    }

    private String convertConstraintToMessage(ConstraintViolation<UserDto> constraint) {
        return STR. "\{ StringUtils.capitalize(constraint.getPropertyPath().toString()) } \{ constraint.getMessage() }" .trim();
    }
}
