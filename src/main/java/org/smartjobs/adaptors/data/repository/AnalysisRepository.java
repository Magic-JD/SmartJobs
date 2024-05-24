package org.smartjobs.adaptors.data.repository;

import org.smartjobs.adaptors.data.repository.data.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}
