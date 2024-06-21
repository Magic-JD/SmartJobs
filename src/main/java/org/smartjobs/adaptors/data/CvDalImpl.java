package org.smartjobs.adaptors.data;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.data.repository.CandidateRepository;
import org.smartjobs.adaptors.data.repository.CvRepository;
import org.smartjobs.adaptors.data.repository.data.Candidate;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.smartjobs.core.config.DateSupplier;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.CvData;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.ports.dal.CvDal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class CvDalImpl implements CvDal {


    private final CvRepository cvRepository;
    private final CandidateRepository candidateRepository;
    private final DateSupplier dateSupplier;

    @Autowired
    public CvDalImpl(CvRepository repository, CandidateRepository candidateRepository, DateSupplier dateSupplier) {
        this.cvRepository = repository;
        this.candidateRepository = candidateRepository;
        this.dateSupplier = dateSupplier;
    }

    @Override
    @Transactional
    public void addCvsToRepository(long userId, long roleId, List<ProcessedCv> processedCvs) {
        for (ProcessedCv pc : processedCvs) {
            Long cvId = this.getByHash(pc.fileHash())
                    .map(CvData::id)
                    .orElseGet(() -> cvRepository.save(
                            Cv.builder()
                                    .fileHash(pc.fileHash())
                                    .condensedText(pc.condensedDescription())
                                    .build())
                            .getId());
            Candidate candidate = Candidate.builder()
                    .name(pc.name())
                    .cvId(cvId)
                    .lastAccessed(dateSupplier.getDate())
                    .userId(userId)
                    .roleId(roleId)
                    .currentlySelected(pc.currentlySelected())
                    .build();
            Candidate savedCandidate = candidateRepository.save(candidate);
            log.debug("Saved Cv: {} Candidate: {}", cvId, savedCandidate.getId());
        }
    }

    @Override
    public List<CandidateData> getAllCandidates(long userId, long roleId) {
        return candidateRepository.findAllByUserIdAndRoleId(userId, roleId).stream()
                .map(candidate -> new CandidateData(candidate.getId(), candidate.getName(), candidate.getUserId(), candidate.getRoleId(), candidate.getCurrentlySelected()))
                .toList();
    }

    @Override
    public List<ProcessedCv> getAllSelected(long userId, long roleId) {
        return cvRepository.findByCurrentlySelected(true, userId, roleId)
                .stream()
                .map(cv -> new ProcessedCv(
                        (Long) cv.get("id"),
                        (String) cv.get("name"),
                        true,
                        (String) cv.get("file_hash"),
                        (String) cv.get("condensed_text")))
                .toList();
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
        return candidateRepository.updateCurrentlySelectedById(cvId, select)
                .map(candidate -> new CandidateData(candidate.getId(), candidate.getName(), candidate.getUserId(), candidate.getRoleId(), candidate.getCurrentlySelected()));
    }

    @Override
    public int findSelectedCandidateCount(long userId, long roleId) {
        return candidateRepository.countByCurrentlySelectedAndUserIdAndRoleId(true, userId, roleId);
    }

    @Override
    public void deleteAllCandidates(long userId, long roleId) {
        candidateRepository.deleteByUserIdAndRoleId(userId, roleId);
    }

    @Override
    @Transactional
    public Optional<CvData> getByHash(String hash) {
        return cvRepository.findByFileHash(hash).stream().findAny()
                .map(cv -> new CvData(
                        cv.getId(),
                        cv.getFileHash(),
                        cv.getCondensedText()));

    }

    @Override
    public List<CandidateData> getByCvId(Long id) {
        return candidateRepository.findAllByCvId(id).stream().map(candidate -> new CandidateData(candidate.getId(), candidate.getName(), candidate.getUserId(), candidate.getRoleId(), candidate.getCurrentlySelected())).toList();
    }
}
