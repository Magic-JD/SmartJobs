package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.DefinedScoringCriteriaRepository;
import org.smartjobs.core.dal.DefinedScoringCriteriaDao;
import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefinedScoringCriteriaDaoImpl implements DefinedScoringCriteriaDao {

    private final DefinedScoringCriteriaRepository repository;

    @Autowired
    public DefinedScoringCriteriaDaoImpl(DefinedScoringCriteriaRepository repository) {
        this.repository = repository;
    }

    @Override
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
