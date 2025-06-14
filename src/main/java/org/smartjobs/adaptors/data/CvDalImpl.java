package org.smartjobs.adaptors.data;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.data.repository.CandidateRepository;
import org.smartjobs.adaptors.data.repository.CvRepository;
import org.smartjobs.adaptors.data.repository.RoleRepository;
import org.smartjobs.adaptors.data.repository.data.Candidate;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.smartjobs.adaptors.data.repository.data.Role;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.CvData;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.ports.dal.CvDal;
import org.smartjobs.core.provider.DateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class CvDalImpl implements CvDal {


    private final CvRepository cvRepository;
    private final CandidateRepository candidateRepository;
    private final DateProvider dateProvider;
    private final RoleRepository roleRepository;

    @Autowired
    public CvDalImpl(CvRepository repository, CandidateRepository candidateRepository, DateProvider dateProvider, RoleRepository roleRepository) {
        this.cvRepository = repository;
        this.candidateRepository = candidateRepository;
        this.dateProvider = dateProvider;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void addCvsToRepository(long userId, long roleId, List<ProcessedCv> processedCvs) {
        for (ProcessedCv pc : processedCvs) {
            Cv cv = cvRepository.findByFileHash(pc.fileHash()).orElseGet(() -> cvRepository.save(Cv.builder().fileHash(pc.fileHash()).condensedText(pc.condensedDescription()).build()));
            Role role = roleRepository.getReferenceById(roleId);
            Candidate candidate = Candidate.builder().name(pc.name()).cv(cv).lastAccessed(dateProvider.provideDate()).userId(userId).role(role).currentlySelected(pc.currentlySelected()).build();
            Candidate savedCandidate = candidateRepository.save(candidate);
            log.debug("Saved Cv: {} Candidate: {}", cv.getId(), savedCandidate.getId());
        }
    }

    @Override
    public List<CandidateData> getAllCandidates(long userId, long roleId) {
        return candidateRepository.findAllByUserIdAndRoleId(userId, roleId).stream().map(this::convertToCandidateData).toList();
    }

    @Override
    public List<ProcessedCv> getAllSelected(long userId, long roleId) {
        return cvRepository.findByCurrentlySelected(true, userId, roleId).stream().map(cv -> new ProcessedCv(cv.getId(), cv.getCandidate().getName(), cv.getCandidate().getCurrentlySelected(), cv.getFileHash(), cv.getCondensedText())).toList();
    }

    @Override
    public void deleteByCandidateId(long candidateId) {
        candidateRepository.deleteById(candidateId);
    }


    @Override
    public boolean knownHash(String fileHash) {
        return cvRepository.existsCvByFileHash(fileHash);
    }

    @Override
    public Optional<CandidateData> updateCurrentlySelectedById(long cvId, boolean select) {
        return candidateRepository.updateCurrentlySelectedById(cvId, select).map(this::convertToCandidateData);
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
                candidate.getRole().getId(),
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
    @Transactional
    public Optional<CvData> getByHash(String hash) {
        return cvRepository.findByFileHash(hash).stream().findAny().map(cv -> new CvData(cv.getId(), cv.getFileHash(), cv.getCondensedText()));

    }

    @Override
    public List<CandidateData> getByCvId(Long id) {
        return candidateRepository.findAllByCvId(id).stream().map(this::convertToCandidateData).toList();
    }
}
