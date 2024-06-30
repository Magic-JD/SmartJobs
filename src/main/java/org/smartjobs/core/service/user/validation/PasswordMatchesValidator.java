package org.smartjobs.core.service.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        //This method is not necessary (I guess)
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDto user = (UserDto) obj;
        if (user.password() == null) return false; // This can be null as this is run as part of the validation steps.
        return user.password().equals(user.matchingPassword());
    }
}