package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.CredentialRepository;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.ports.dal.CredentialDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CredentialDaoImpl implements CredentialDao {

    private final CredentialRepository repository;

    @Autowired
    public CredentialDaoImpl(CredentialRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> getUser(String username) {
        return repository.findByUsername(username).map(user -> new User(user.getUsername(), user.getPassword(), user.getId(), List.of((GrantedAuthority) () -> "USER")));
    }
}
