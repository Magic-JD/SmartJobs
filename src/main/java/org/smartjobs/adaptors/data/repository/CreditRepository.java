package org.smartjobs.adaptors.data.repository;

import org.smartjobs.adaptors.data.repository.data.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    List<Credit> findByUsername(String username);

}
