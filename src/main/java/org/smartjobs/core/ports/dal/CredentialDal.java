package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.User;

import java.util.Optional;

public interface CredentialDal {

    Optional<User> getUser(String username);

    boolean setUser(String username, String password);
}
