package org.smartjobs.com.dal.repository;

import org.smartjobs.com.dal.repository.data.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    Optional<Credential> findByUsername(String username);

}
