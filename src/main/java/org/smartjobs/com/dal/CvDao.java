package org.smartjobs.com.dal;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.dal.repository.CvRepository;
import org.smartjobs.com.dal.repository.data.Cv;
import org.smartjobs.com.service.candidate.data.CandidateData;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CvDao {

    private static final Logger logger = LoggerFactory.getLogger(CvDao.class);

    private final CvRepository repository;

    @Autowired
    public CvDao(CvRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void addCvsToRepository(String username, long roleId, List<ProcessedCv> processedCvs) {
        List<Cv> cvs = processedCvs.stream().map(cv -> Cv.builder()
                .candidateName(cv.name())
                .fileHash(cv.fileHash())
                .currentlySelected(cv.currentlySelected())
                .condensedText(cv.condensedDescription())
                .username(username)
                .roleId(roleId)
                .build()).toList();
        logger.debug("Preparing to save candidate CVs as: {}", cvs);
        repository.saveAllAndFlush(cvs);
    }

    public List<CandidateData> getAllNames(String userName, Long roleId) {
        return repository.findAllProjectedAsCandidateData(userName, roleId);
    }

    public List<ProcessedCv> getAllSelected(String userName, Long roleId) {
        return repository.findByCurrentlySelected(true, userName, roleId)
                .stream()
                .map(cv -> new ProcessedCv(
                        cv.getId(),
                        cv.getCandidateName(),
                        cv.getCurrentlySelected(),
                        cv.getFileHash(),
                        cv.getCondensedText()))
                .toList();
    }

    public void deleteByCvId(long cvId) {
        repository.deleteById(cvId);
    }


    public boolean knownHash(String fileHash) {
        return repository.existsCvByFileHash(fileHash);
    }

    public Optional<CandidateData> updateCurrentlySelectedById(long cvId, boolean select) {
        repository.updateCurrentlySelectedById(cvId, select);
        return repository.findCandidateDataById(cvId);
    }

    public int findSelectedCandidateCount(String username, long roleId) {
        return repository.countByCurrentlySelectedAndUsernameAndRoleId(true, username, roleId);
    }

    public void deleteAllCandidates(String username, Long roleId) {
        repository.deleteByUsernameAndRoleId(username, roleId);
    }
}
