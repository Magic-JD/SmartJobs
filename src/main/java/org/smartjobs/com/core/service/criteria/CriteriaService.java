package org.smartjobs.com.core.service.criteria;

import org.smartjobs.com.core.cache.ScoringCriteriaCache;
import org.smartjobs.com.core.dal.UserCriteriaDao;
import org.smartjobs.com.core.entities.DefinedScoringCriteria;
import org.smartjobs.com.core.exception.categories.UserResolvedExceptions.NoScoreProvidedException;
import org.smartjobs.com.core.exception.categories.UserResolvedExceptions.NoValueProvidedException;
import org.smartjobs.com.core.exception.categories.UserResolvedExceptions.ScoreIsNotNumberException;
import org.smartjobs.com.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CriteriaService {

    private final ScoringCriteriaCache cache;
    private final UserCriteriaDao userCriteriaDao;

    @Autowired
    public CriteriaService(ScoringCriteriaCache cache, UserCriteriaDao userCriteriaDao) {
        this.cache = cache;
        this.userCriteriaDao = userCriteriaDao;
    }

    public List<DefinedScoringCriteria> getScoringCriteriaForCategory(CriteriaCategory category) {
        return cache.getAllDefinedScoringCriteria().stream().filter(sc -> sc.category().equals(category)).toList();
    }

    public DefinedScoringCriteria getCriteriaById(long criteriaId) {
        return cache.getDefinedScoringCriteriaById(criteriaId);
    }

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

    public void deleteUserCriteria(Long criteriaId) {
        userCriteriaDao.deleteUserCriteria(criteriaId);
    }

    public record UserCriteria(long id, long definedCriteriaId, Optional<String> value, int score) {
    }
}
