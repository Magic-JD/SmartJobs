package org.smartjobs.com.adaptors.data;

import org.smartjobs.com.adaptors.data.repository.UserCriteriaRepository;
import org.smartjobs.com.adaptors.data.repository.data.UserCriteria;
import org.smartjobs.com.core.dal.UserCriteriaDao;
import org.smartjobs.com.core.service.criteria.CriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserCriteriaDaoImpl implements UserCriteriaDao {

    private final UserCriteriaRepository repository;

    @Autowired
    public UserCriteriaDaoImpl(UserCriteriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public CriteriaService.UserCriteria createNewUserCriteria(long definedCriteriaId, String value, int score) {
        UserCriteria userCriteria = repository.saveAndFlush(UserCriteria.builder().definedCriteriaId(definedCriteriaId).value(value).score(score).build());
        return new CriteriaService.UserCriteria(userCriteria.getId(), userCriteria.getDefinedCriteriaId(), Optional.ofNullable(userCriteria.getValue()), userCriteria.getScore());
    }

    @Override
    public List<CriteriaService.UserCriteria> selectUserCriteriaByIds(List<Long> ids) {
        return repository.findAllById(ids).stream()
                .map(uc -> new CriteriaService.UserCriteria(uc.getId(), uc.getDefinedCriteriaId(), Optional.ofNullable(uc.getValue()), uc.getScore()))
                .toList();
    }

    @Override
    public void deleteUserCriteria(Long criteriaId) {
        repository.deleteById(criteriaId);
    }
}
