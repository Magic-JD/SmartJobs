package org.smartjobs.adaptors.data;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.data.repository.CandidateRepository;
import org.smartjobs.adaptors.data.repository.RoleRepository;
import org.smartjobs.adaptors.data.repository.data.Candidate;
import org.smartjobs.adaptors.data.repository.data.Role;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.ports.dal.CandidateDal;
import org.smartjobs.core.provider.DateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class CandidateDalImpl implements CandidateDal {


    private final CandidateRepository candidateRepository;
    private final DateProvider dateProvider;
    private final RoleRepository roleRepository;

    @Autowired
    public CandidateDalImpl(CandidateRepository candidateRepository, DateProvider dateProvider, RoleRepository roleRepository) {
        this.candidateRepository = candidateRepository;
        this.dateProvider = dateProvider;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void addCvsToRepository(long userId, long roleId, List<ProcessedCv> processedCvs) {
        Role role = roleRepository.getReferenceById(roleId);
        for (ProcessedCv pc : processedCvs) {
            Candidate candidate = Candidate.builder()
                    .name(pc.name())
                    .fileHash(pc.fileHash())
                    .name(pc.name())
                    .condensedText(pc.condensedDescription())
                    .lastAccessed(dateProvider.provideDate())
                    .userId(userId)
                    .role(role)
                    .currentlySelected(pc.currentlySelected())
                    .build();
            Candidate savedCandidate = candidateRepository.save(candidate);
            log.debug("Saved Candidate: {}", savedCandidate.getId());
        }
    }

    @Override
    public List<CandidateData> getAllCandidates(long userId, long roleId) {
        return candidateRepository.findAllByUserIdAndRoleId(userId, roleId).stream().map(this::convertToCandidateData).toList();
    }

    @Override
    public List<ProcessedCv> getAllSelected(long userId, long roleId) {
        return candidateRepository.findByCurrentlySelected(true, userId, roleId).stream().map(candidate -> new ProcessedCv(candidate.getId(), candidate.getName(), candidate.getCurrentlySelected(), candidate.getFileHash(), candidate.getCondensedText())).toList();
    }

    @Override
    public void deleteByCandidateId(long candidateId) {
        candidateRepository.deleteById(candidateId);
    }


    @Override
    public Optional<CandidateData> updateCurrentlySelectedById(long candidateId, boolean select) {
        return candidateRepository.updateCurrentlySelectedById(candidateId, select).map(this::convertToCandidateData);
    }

    @Override
    public List<CandidateData> updateCurrentlySelectedAll(long userId, long roleId, boolean select) {
        return candidateRepository.updateCurrentlySelectedAll(userId, roleId, select).stream().map(this::convertToCandidateData).toList();
    }

    private CandidateData convertToCandidateData(Candidate candidate) {
        return new CandidateData(
                candidate.getId(),
                candidate.getName(),
                candidate.getUserId(),
                candidate.getCurrentlySelected()
        );
    }

    @Override
    public int findSelectedCandidateCount(long userId, long roleId) {
        return candidateRepository.countByCurrentlySelectedAndUserIdAndRoleId(true, userId, roleId);
    }

    @Override
    public void deleteAllCandidates(long userId, long roleId) {
        candidateRepository.deleteByCurrentlySelectedAndUserIdAndRoleId(true, userId, roleId);
    }

    @Override
    public Optional<CandidateData> getByCandidateId(Long id) {
        return candidateRepository.findById(id).map(this::convertToCandidateData);
    }
}
