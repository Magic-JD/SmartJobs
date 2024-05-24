package org.smartjobs.core.service.user;

import org.smartjobs.core.entities.User;
import org.smartjobs.core.ports.dal.CredentialDal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final CredentialDal credentialDal;

    @Autowired
    public UserService(CredentialDal credentialDal) {
        this.credentialDal = credentialDal;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialDal.getUser(username).orElseThrow(() -> new UsernameNotFoundException("The user could not be found"));
    }
}
