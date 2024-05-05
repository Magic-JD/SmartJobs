package org.smartjobs.adaptors.data;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.adaptors.data.repository.CvRepository;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.smartjobs.core.dal.CvDao;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.ProcessedCv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CvDaoImpl implements CvDao {

    private static final Logger logger = LoggerFactory.getLogger(CvDaoImpl.class);

    private final CvRepository repository;

    @Autowired
    public CvDaoImpl(CvRepository repository) {
        this.repository = repository;
    }

    @Override
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

    @Override
    public List<CandidateData> getAllNames(String userName, Long roleId) {
        return repository.findAllProjectedAsCandidateData(userName, roleId);
    }

    @Override
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
    public int findSelectedCandidateCount(String username, long roleId) {
        return repository.countByCurrentlySelectedAndUsernameAndRoleId(true, username, roleId);
    }

    @Override
    public void deleteAllCandidates(String username, Long roleId) {
        repository.deleteByUsernameAndRoleId(username, roleId);
    }
}
