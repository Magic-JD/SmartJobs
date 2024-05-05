package org.smartjobs.core.service.auth;

import org.smartjobs.core.exception.categories.UserResolvedExceptions.IncorrectAuthenticationException;
import org.smartjobs.core.service.AuthService;
import org.smartjobs.core.service.auth.levels.AuthLevel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public String getCurrentUsername() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new IncorrectAuthenticationException();
    }

    @Override
    public AuthLevel userMaxAuthLevel() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream().map(ga -> AuthLevel.valueOf(ga.getAuthority()))
                .max(Comparator.comparing(AuthLevel::getLevelNumber))
                .orElseThrow(IncorrectAuthenticationException::new);
    }
}
