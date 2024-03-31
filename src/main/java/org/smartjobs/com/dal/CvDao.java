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
    public void addCvsToRepository(List<ProcessedCv> processedCvs) {
        List<Cv> cvs = processedCvs.stream().map(cv -> Cv.builder()
                .candidateName(cv.name())
                .fileHash(cv.fileHash())
                .currentlySelected(cv.currentlySelected())
                .condensedText(cv.condensedDescription())
                .build()).toList();
        logger.debug("Preparing to save candidate CVs as: {}", cvs);
        repository.saveAllAndFlush(cvs);
    }

    public List<CandidateData> getAllNames() {
        return repository.findAllProjectedAsCandidateData();
    }

    public List<ProcessedCv> getAllSelected() {
        return repository.findByCurrentlySelected(true)
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

    public int findSelectedCandidateCount() {
        return repository.countByCurrentlySelected(true);
    }
}
