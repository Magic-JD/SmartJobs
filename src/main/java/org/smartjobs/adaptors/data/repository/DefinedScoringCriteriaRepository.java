package org.smartjobs.adaptors.data.repository;

import org.smartjobs.adaptors.data.repository.data.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefinedScoringCriteriaRepository extends JpaRepository<Criteria, Long> {
}
