package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.User;

import java.util.Optional;

public interface CredentialDao {

    Optional<User> getUser(String username);
}
