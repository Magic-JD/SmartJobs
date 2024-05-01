package org.smartjobs.com.service.criteria;

import org.smartjobs.com.cache.ScoringCriteriaCache;
import org.smartjobs.com.service.role.data.CriteriaCategory;
import org.smartjobs.com.service.role.data.DefinedScoringCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CriteriaService {

    private final ScoringCriteriaCache cache;

    @Autowired
    public CriteriaService(ScoringCriteriaCache cache) {
        this.cache = cache;
    }

    public List<DefinedScoringCriteria> getScoringCriteriaForCategory(CriteriaCategory category) {
        return cache.getAllDefinedScoringCriteria().stream().filter(sc -> sc.category().equals(category)).toList();
    }
}
