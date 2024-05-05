package org.smartjobs.core.service.criteria;

import org.smartjobs.core.cache.ScoringCriteriaCache;
import org.smartjobs.core.dal.UserCriteriaDao;
import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.UserCriteria;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoScoreProvidedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoValueProvidedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.ScoreIsNotNumberException;
import org.smartjobs.core.service.CriteriaService;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CriteriaServiceImpl implements CriteriaService {

    private final ScoringCriteriaCache cache;
    private final UserCriteriaDao userCriteriaDao;

    @Autowired
    public CriteriaServiceImpl(ScoringCriteriaCache cache, UserCriteriaDao userCriteriaDao) {
        this.cache = cache;
        this.userCriteriaDao = userCriteriaDao;
    }

    @Override
    public List<DefinedScoringCriteria> getScoringCriteriaForCategory(CriteriaCategory category) {
        return cache.getAllDefinedScoringCriteria().stream().filter(sc -> sc.category().equals(category)).toList();
    }

    @Override
    public DefinedScoringCriteria getCriteriaById(long criteriaId) {
        return cache.getDefinedScoringCriteriaById(criteriaId);
    }

    @Override
    public UserCriteria createUserCriteria(long criteriaId, String value, String score) {
        DefinedScoringCriteria definedCriteria = cache.getDefinedScoringCriteriaById(criteriaId);
        if (!StringUtils.hasText(score)) {
            throw new NoScoreProvidedException();
        }
        int scoreInt;
        try {
            scoreInt = Integer.parseInt(score);
        } catch (NumberFormatException e) {
            throw new ScoreIsNotNumberException();
        }
        if (definedCriteria.needsInput() && !StringUtils.hasText(value)) {
            throw new NoValueProvidedException();
        }

        return userCriteriaDao.createNewUserCriteria(criteriaId, value, scoreInt);
    }

    @Override
    public void deleteUserCriteria(Long criteriaId) {
        userCriteriaDao.deleteUserCriteria(criteriaId);
    }

}
