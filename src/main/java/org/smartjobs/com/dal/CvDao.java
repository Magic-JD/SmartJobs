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

@Component
public class CvDao {

    private static final Logger logger = LoggerFactory.getLogger(CvDao.class);

    private final CvRepository repository;

    @Autowired
    public CvDao(CvRepository repository) {
        this.repository = repository;
    }

    public void addCvsToRepository(List<ProcessedCv> processedCvs) {
        List<Cv> cvs = processedCvs.stream().map(cv -> Cv.builder()
                .candidateName(cv.name())
                .fullText(cv.fullDescription())
                .condensedText(cv.condensedDescription())
                .filePath(cv.fileLocation())
                .build()).toList();
        logger.debug("Preparing to save candidate CVs as: {}", cvs);
        repository.saveAllAndFlush(cvs);
    }

    public List<CandidateData> getAllNames() {
        return repository.findAll().stream().map(cv -> new CandidateData(cv.getCandidateName(), cv.getFilePath())).toList();
    }

    public List<ProcessedCv> getAll() {
        return repository.findAll()
                .stream()
                .map(cv -> new ProcessedCv(
                        cv.getCandidateName(),
                        cv.getFilePath(),
                        cv.getCondensedText(),
                        cv.getFullText()))
                .toList();
    }

    @Transactional
    public void deleteByFilePath(String filePath) {
        repository.deleteByFilePath(filePath);
    }

    public void addCvToRepository(ProcessedCv cv) {
        repository.saveAndFlush(Cv.builder()
                .candidateName(cv.name())
                .fullText(cv.fullDescription())
                .condensedText(cv.condensedDescription())
                .filePath(cv.fileLocation())
                .build());

    }
}
