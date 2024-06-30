package org.smartjobs.core.service.user.validation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record UserDto(

        @NotNull
        @NotEmpty
        String username,

        @NotNull
        @NotEmpty
        @Size(min = 9, message = "must be more than 8 characters")
        String password,
        String matchingPassword
) {
}