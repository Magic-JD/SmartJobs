package org.smartjobs.com.adaptors.data.repository;

import org.smartjobs.com.adaptors.data.repository.data.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    Optional<Credential> findByUsername(String username);

}
