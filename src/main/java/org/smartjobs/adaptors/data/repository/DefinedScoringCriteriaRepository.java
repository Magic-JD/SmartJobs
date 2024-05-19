package org.smartjobs.adaptors.data.repository;

import org.smartjobs.adaptors.data.repository.data.DefinedCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefinedScoringCriteriaRepository extends JpaRepository<DefinedCriteria, Long> {
}
