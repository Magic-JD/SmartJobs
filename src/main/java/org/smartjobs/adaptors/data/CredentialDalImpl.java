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
        return repository.findByUsername(username)
                .map(user -> new User(user.getUsername(), user.getPassword(), user.getId(), getGrantedAuthority(user.getAuthority())));
    }


    @Override
    public User setUser(String username, String password) {
        Credential credential = Credential.builder().username(username).password(password).authority("USER").build();
        Credential saved = repository.save(credential);
        return new User(saved.getUsername(), saved.getPassword(), saved.getId(), getGrantedAuthority(saved.getAuthority()));
    }

    private static List<GrantedAuthority> getGrantedAuthority(String authority) {
        return List.of((GrantedAuthority) () -> "ROLE_" + authority);
    }
}
