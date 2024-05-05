package org.smartjobs.com.service.candidate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.client.gpt.AiClient;
import org.smartjobs.com.dal.CvDao;
import org.smartjobs.com.service.candidate.data.CandidateData;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.file.data.FileInformation;
import org.smartjobs.com.utils.ConcurrencyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final AiClient client;
    private final CvDao cvDao;

    @Autowired
    public CandidateService(AiClient client, CvDao cvDao) {
        this.client = client;
        this.cvDao = cvDao;
    }

    public List<ProcessedCv> getFullCandidateInfo(String userName, Long roleId) {
        return cvDao.getAllSelected(userName, roleId);
    }

    public List<CandidateData> getCurrentCandidates(String userName, Long role) {
        return cvDao.getAllNames(userName, role);
    }

    public void updateCandidateCvs(String username, Long roleId, List<Optional<FileInformation>> fileInformationList) {
        var processedCvs = ConcurrencyUtil.virtualThreadList(fileInformationList, fileInformation -> fileInformation.flatMap(this::processCv));
        List<ProcessedCv> list = processedCvs.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        if (processedCvs.isEmpty()) {
            return;
        }
        cvDao.addCvsToRepository(username, roleId, list);
    }

    public void deleteCandidate(String username, long cvId) {
        cvDao.deleteByCvId(cvId);
    }

    private Optional<ProcessedCv> processCv(FileInformation fileInformation) {
        if (cvDao.knownHash(fileInformation.fileHash())) {
            return Optional.empty();
        }
        var nameFuture = supplyAsync(() -> client.extractCandidateName(fileInformation.fileContent()));
        var descriptionFuture = supplyAsync(() -> client.anonymousCandidateDescription(fileInformation.fileContent()));
        var name = nameFuture.join();
        var cvDescription = descriptionFuture.join();
        if (name.isEmpty() || cvDescription.isEmpty()) {
            logger.error("Either CV position {} or description {} is empty.", name.orElse("???"), cvDescription.orElse("???"));
            return Optional.empty();
        } else {
            return Optional.of(new ProcessedCv(null, name.get(), true, fileInformation.fileHash(), cvDescription.get()));
        }
    }

    public Optional<CandidateData> toggleCandidateSelect(String currentUsername, long cvId, boolean select) {
        return cvDao.updateCurrentlySelectedById(cvId, select);
    }

    public int findSelectedCandidateCount(String username, long currentRole) {
        return cvDao.findSelectedCandidateCount(username, currentRole);
    }

    public void deleteAllCandidates(String username, Long roleId) {
        cvDao.deleteAllCandidates(username, roleId);
    }
}
