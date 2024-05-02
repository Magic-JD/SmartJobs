package org.smartjobs.com.dal;

import org.smartjobs.com.dal.repository.DefinedScoringCriteriaRepository;
import org.smartjobs.com.service.criteria.data.DefinedScoringCriteria;
import org.smartjobs.com.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefinedScoringCriteriaDao {

    private final DefinedScoringCriteriaRepository repository;

    @Autowired
    public DefinedScoringCriteriaDao(DefinedScoringCriteriaRepository repository) {
        this.repository = repository;
    }

    public List<DefinedScoringCriteria> getAllDefinedScoringCriteria() {
        return repository.findAll().stream()
                .map(dsc -> new DefinedScoringCriteria(dsc.getId(),
                        dsc.getCriteria(),
                        CriteriaCategory.getFromName(dsc.getCategory()),
                        dsc.isInput(),
                        dsc.getAiPrompt()))
                .toList();
    }
}
