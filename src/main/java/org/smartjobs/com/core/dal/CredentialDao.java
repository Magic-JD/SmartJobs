package org.smartjobs.com.core.dal;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface CredentialDao {
    Optional<UserDetails> getUserPassword(String username);
}
