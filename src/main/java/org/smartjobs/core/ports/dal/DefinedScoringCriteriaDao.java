package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface DefinedScoringCriteriaDao {

    @Cacheable(value = "defined-criteria", cacheManager = "staticCacheManager", sync = true)
    List<DefinedScoringCriteria> getAllDefinedScoringCriteria();
}
