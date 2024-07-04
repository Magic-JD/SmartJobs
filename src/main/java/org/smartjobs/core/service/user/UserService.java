package org.smartjobs.core.service.user;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.ValidateEmailEvent;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.UserAlreadyExistsException;
import org.smartjobs.core.ports.dal.CredentialDal;
import org.smartjobs.core.service.UserRegistration;
import org.smartjobs.core.service.user.validation.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService, UserRegistration {

    private final CredentialDal credentialDal;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final EventEmitter eventEmitter;
    private final CodeSupplier codeSupplier;
    private final Cache emailValidationCache;

    @Autowired
    public UserService(CredentialDal credentialDal, PasswordEncoder passwordEncoder, Validator validator, EventEmitter eventEmitter, CodeSupplier codeSupplier, Cache emailValidationCache) {
        this.credentialDal = credentialDal;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.eventEmitter = eventEmitter;
        this.codeSupplier = codeSupplier;
        this.emailValidationCache = emailValidationCache;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        username = username.toLowerCase();
        return credentialDal.getUser(username).orElseThrow(() -> new UsernameNotFoundException("The user could not be found"));
    }

    @Override
    @Transactional
    public List<String> validateUser(UserDto userDto) throws UserAlreadyExistsException {
        var errors = validator.validate(userDto).stream()
                .map(this::convertConstraintToMessage)
                .sorted()
                .toList();
        if (!errors.isEmpty()) {
            return errors;
        }
        String username = userDto.email().toLowerCase();
        if (userExists(username)) {
            return List.of("An account for that email already exists");
        }
        String password = passwordEncoder.encode(userDto.password());
        String code = codeSupplier.createCode();
        eventEmitter.sendEvent(new ValidateEmailEvent(username, code));
        emailValidationCache.put(code, new UnamePword(username, password));
        return Collections.emptyList();
    }

    @Override
    public boolean createUser(String code) {
        var unamePwordOption = Optional.ofNullable(emailValidationCache.get(code))
                .map(Cache.ValueWrapper::get)
                .filter(UnamePword.class::isInstance)
                .map(UnamePword.class::cast);
        if (unamePwordOption.isEmpty()) return false;
        emailValidationCache.evict(code);
        UnamePword unamePword = unamePwordOption.get();
        credentialDal.setUser(unamePword.username(), unamePword.password());
        return true;
    }

    private boolean userExists(String email) {
        return credentialDal.getUser(email).isPresent();
    }

    private String convertConstraintToMessage(ConstraintViolation<UserDto> constraint) {
        return STR. "\{ StringUtils.capitalize(constraint.getPropertyPath().toString()) } \{ constraint.getMessage() }" .trim();
    }

    private record UnamePword(String username, String password) {
    }
}
