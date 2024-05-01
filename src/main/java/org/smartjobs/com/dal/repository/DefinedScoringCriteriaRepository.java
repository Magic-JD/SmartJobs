package org.smartjobs.com.dal.repository;

import org.smartjobs.com.dal.repository.data.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefinedScoringCriteriaRepository extends JpaRepository<Criteria, Long> {
}
