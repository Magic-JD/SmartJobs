package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.CredentialRepository;
import org.smartjobs.adaptors.data.repository.data.Credential;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.ports.dal.CredentialDal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CredentialDalImpl implements CredentialDal {

    private final CredentialRepository repository;

    @Autowired
    public CredentialDalImpl(CredentialRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> getUser(String username) {
        return repository.findByUsername(username).map(user -> new User(user.getUsername(), user.getPassword(), user.getId(),
                List.of((GrantedAuthority) () -> "ROLE_" + user.getAuthority())));
    }

    @Override
    public boolean setUser(String username, String password) {
        Credential credential = Credential.builder().username(username).password(password).authority("USER").build();
        repository.save(credential);
        return true;
    }
}
