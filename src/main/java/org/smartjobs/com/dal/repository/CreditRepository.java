package org.smartjobs.com.dal.repository;

import org.smartjobs.com.dal.repository.data.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    List<Credit> findByUsername(String username);

}
