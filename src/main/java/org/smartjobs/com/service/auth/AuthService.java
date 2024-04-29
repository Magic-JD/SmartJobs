package org.smartjobs.com.service.auth;

import org.smartjobs.com.exception.categories.UserResolvedExceptions.IncorrectAuthenticationException;
import org.smartjobs.com.service.auth.levels.AuthLevel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AuthService {

    public String getCurrentUsername() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new IncorrectAuthenticationException();
    }

    public AuthLevel userMaxAuthLevel() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream().map(ga -> AuthLevel.valueOf(ga.getAuthority()))
                .max(Comparator.comparing(AuthLevel::getLevelNumber))
                .orElseThrow(IncorrectAuthenticationException::new);
    }
}
