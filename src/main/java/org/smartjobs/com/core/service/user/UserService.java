package org.smartjobs.com.core.service.user;

import org.smartjobs.com.core.dal.CredentialDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final CredentialDao credentialDao;

    @Autowired
    public UserService(CredentialDao credentialDao) {
        this.credentialDao = credentialDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialDao.getUserPassword(username).orElseThrow(() -> new UsernameNotFoundException("The user could not be found"));
    }
}
