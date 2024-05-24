package org.smartjobs.core.service;

import io.vavr.control.Either;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.failures.ProcessFailure;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    List<ProcessedCv> getFullCandidateInfo(long userId, long roleId);

    List<CandidateData> getCurrentCandidates(long userId, long role);

    List<ProcessedCv> updateCandidateCvs(long userId, long roleId, List<Either<ProcessFailure, FileInformation>> fileInformationList);

    void deleteCandidate(long userId, long currentRole, long roleId);

    Optional<CandidateData> toggleCandidateSelect(long userId, long roleId, long cvId, boolean select);

    int findSelectedCandidateCount(long userId, long roleId);

    void deleteAllCandidates(long userId, long roleId);
}
