package org.smartjobs.com.dal;

import org.smartjobs.com.dal.repository.UserCriteriaRepository;
import org.smartjobs.com.dal.repository.data.UserCriteria;
import org.smartjobs.com.service.criteria.CriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserCriteriaDao {

    private final UserCriteriaRepository repository;

    @Autowired
    public UserCriteriaDao(UserCriteriaRepository repository) {
        this.repository = repository;
    }

    public CriteriaService.UserCriteria createNewUserCriteria(long definedCriteriaId, String value, int score) {
        UserCriteria userCriteria = repository.saveAndFlush(UserCriteria.builder().definedCriteriaId(definedCriteriaId).value(value).score(score).build());
        return new CriteriaService.UserCriteria(userCriteria.getId(), userCriteria.getDefinedCriteriaId(), Optional.ofNullable(userCriteria.getValue()), userCriteria.getScore());
    }

    public List<CriteriaService.UserCriteria> selectUserCriteriaByIds(List<Long> ids) {
        return repository.findAllById(ids).stream()
                .map(uc -> new CriteriaService.UserCriteria(uc.getId(), uc.getDefinedCriteriaId(), Optional.ofNullable(uc.getValue()), uc.getScore()))
                .toList();
    }

    public void deleteUserCriteria(Long criteriaId) {
        repository.deleteById(criteriaId);
    }
}
