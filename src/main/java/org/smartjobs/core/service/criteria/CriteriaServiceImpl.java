package org.smartjobs.core.service.criteria;

import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.entities.UserCriteria;
import org.smartjobs.core.exception.categories.ApplicationExceptions.IncorrectIdForDefinedScoringCriteriaException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoScoreProvidedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoValueProvidedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.ScoreIsNotNumberException;
import org.smartjobs.core.ports.dal.DefinedScoringCriteriaDao;
import org.smartjobs.core.ports.dal.UserCriteriaDao;
import org.smartjobs.core.service.CriteriaService;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CriteriaServiceImpl implements CriteriaService {

    private final UserCriteriaDao userCriteriaDao;
    private final DefinedScoringCriteriaDao definedScoringCriteriaDao;

    @Autowired
    public CriteriaServiceImpl(UserCriteriaDao userCriteriaDao, DefinedScoringCriteriaDao definedScoringCriteriaDao) {
        this.userCriteriaDao = userCriteriaDao;
        this.definedScoringCriteriaDao = definedScoringCriteriaDao;
    }

    @Override
    public List<DefinedScoringCriteria> getScoringCriteriaForCategory(CriteriaCategory category) {
        return getAllDefinedScoringCriteria().stream().filter(sc -> sc.category().equals(category)).toList();
    }

    @Override
    public DefinedScoringCriteria getCriteriaById(long criteriaId) {
        return getAllDefinedScoringCriteria()
                .stream()
                .filter(dc -> dc.id() == criteriaId)
                .findFirst()
                .orElseThrow((() -> new IncorrectIdForDefinedScoringCriteriaException(criteriaId)));
    }

    @Override
    public UserCriteria createUserCriteria(long criteriaId, String value, String score) {
        DefinedScoringCriteria definedCriteria = getCriteriaById(criteriaId);
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

    private List<DefinedScoringCriteria> getAllDefinedScoringCriteria() {
        return definedScoringCriteriaDao.getAllDefinedScoringCriteria();
    }
}
