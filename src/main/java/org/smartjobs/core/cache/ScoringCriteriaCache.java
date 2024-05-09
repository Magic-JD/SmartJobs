package org.smartjobs.core.cache;

import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.exception.categories.ApplicationExceptions.IncorrectIdForDefinedScoringCriteriaException;
import org.smartjobs.core.ports.dal.DefinedScoringCriteriaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ScoringCriteriaCache {

    private final Map<Long, DefinedScoringCriteria> criteriaCache;

    @Autowired
    public ScoringCriteriaCache(DefinedScoringCriteriaDao dao) {
        criteriaCache = dao.getAllDefinedScoringCriteria().stream()
                .collect(Collectors.toMap(DefinedScoringCriteria::id, Function.identity()));
    }

    public DefinedScoringCriteria getDefinedScoringCriteriaById(long id) {
        DefinedScoringCriteria scoringCriteria = criteriaCache.get(id);
        if (scoringCriteria == null) throw new IncorrectIdForDefinedScoringCriteriaException(id);
        return scoringCriteria;
    }

    public Collection<DefinedScoringCriteria> getAllDefinedScoringCriteria() {
        return criteriaCache.values();
    }
}
