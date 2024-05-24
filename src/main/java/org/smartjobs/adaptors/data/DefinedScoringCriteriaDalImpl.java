package org.smartjobs.adaptors.data;

import org.smartjobs.adaptors.data.repository.DefinedScoringCriteriaRepository;
import org.smartjobs.core.entities.DefinedScoringCriteria;
import org.smartjobs.core.ports.dal.DefinedScoringCriteriaDal;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DefinedScoringCriteriaDalImpl implements DefinedScoringCriteriaDal {

    private final DefinedScoringCriteriaRepository repository;

    @Autowired
    public DefinedScoringCriteriaDalImpl(DefinedScoringCriteriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DefinedScoringCriteria> getAllDefinedScoringCriteria() {
        return repository.findAll().stream()
                .map(dsc -> new DefinedScoringCriteria(dsc.getId(),
                        dsc.getCriteria(),
                        CriteriaCategory.getFromName(dsc.getCategory()),
                        dsc.isInput(),
                        Optional.ofNullable(dsc.getInputExample()),
                        dsc.getAiPrompt(),
                        dsc.getTooltip()))
                .toList();
    }
}
