package org.smartjobs.com.adaptors.data;

import org.smartjobs.com.adaptors.data.repository.CredentialRepository;
import org.smartjobs.com.core.dal.CredentialDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
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
    public Optional<UserDetails> getUserPassword(String username) {
        return repository.findByUsername(username).map(user -> new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of((GrantedAuthority) () -> "USER");
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getUsername();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        });
    }
}
