package org.smartjobs.adaptors.data;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.data.repository.CvRepository;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.ports.dal.CvDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class CvDaoImpl implements CvDao {


    private final CvRepository repository;

    @Autowired
    public CvDaoImpl(CvRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void addCvsToRepository(long userId, long roleId, List<ProcessedCv> processedCvs) {
        List<Cv> cvs = processedCvs.stream().map(cv -> Cv.builder()
                .candidateName(cv.name())
                .fileHash(cv.fileHash())
                .currentlySelected(cv.currentlySelected())
                .condensedText(cv.condensedDescription())
                .userId(userId)
                .roleId(roleId)
                .build()).toList();
        log.debug("Preparing to save candidate CVs as: {}", cvs);
        repository.saveAllAndFlush(cvs);
    }

    @Override
    public List<CandidateData> getAllNames(long userId, long roleId) {
        return repository.findAllProjectedAsCandidateData(userId, roleId);
    }

    @Override
    public List<ProcessedCv> getAllSelected(long userId, long roleId) {
        return repository.findByCurrentlySelected(true, userId, roleId)
                .stream()
                .map(cv -> new ProcessedCv(
                        cv.getId(),
                        cv.getCandidateName(),
                        cv.getCurrentlySelected(),
                        cv.getFileHash(),
                        cv.getCondensedText()))
                .toList();
    }

    @Override
    public void deleteByCvId(long cvId) {
        repository.deleteById(cvId);
    }


    @Override
    public boolean knownHash(String fileHash) {
        return repository.existsCvByFileHash(fileHash);
    }

    @Override
    public Optional<CandidateData> updateCurrentlySelectedById(long cvId, boolean select) {
        repository.updateCurrentlySelectedById(cvId, select);
        return repository.findCandidateDataById(cvId);
    }

    @Override
    public int findSelectedCandidateCount(long userId, long roleId) {
        return repository.countByCurrentlySelectedAndUsernameAndRoleId(true, userId, roleId);
    }

    @Override
    public void deleteAllCandidates(long userId, long roleId) {
        repository.deleteByUsernameAndRoleId(userId, roleId);
    }

    @Override
    @Transactional
    public Optional<ProcessedCv> getByHash(String hash) {
        return repository.findByFileHash(hash).stream().findAny()
                .map(cv -> new ProcessedCv(
                        cv.getId(),
                        cv.getCandidateName(),
                        cv.getCurrentlySelected(),
                        cv.getFileHash(),
                        cv.getCondensedText()));

    }
}
