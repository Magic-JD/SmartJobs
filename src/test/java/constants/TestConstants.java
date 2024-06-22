package constants;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.smartjobs.adaptors.data.repository.data.SelectedRole;
import org.smartjobs.core.constants.CreditType;
import org.smartjobs.core.entities.*;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.implementation.EventEmitterImpl;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.ports.dal.*;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.candidate.FileHandler;
import org.smartjobs.core.service.candidate.file.FileHandlerImpl;
import org.smartjobs.core.service.credit.CreditServiceImpl;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class TestConstants {
    public static final String CANDIDATE_NAME = "Joe Daunt";
    public static final String CRITERIA_DESCRIPTION = "Criteria Description";
    public static final long ANALYSIS_ID = 43532L;
    public static final String JUSTIFICATION_POSITIVE = "The candidate meets the criteria";
    public static final String JUSTIFICATION_NEGATIVE = "The candidate does not meet the criteria";
    public static final int SCORE_VALUE_GOOD = 7;
    public static final Score SCORE_GOOD = new Score(JUSTIFICATION_POSITIVE, SCORE_VALUE_GOOD);
    public static final int SCORE_VALUE_BAD = 0;
    public static final Score SCORE_BAD = new Score(JUSTIFICATION_NEGATIVE, SCORE_VALUE_BAD);
    public static final long USER_CRITERIA_ID = 22342L;
    public static final String CRITERIA_REQUEST_SCORE = "Does the candidate match the criteria score?";
    public static final String CRITERIA_REQUEST_PASS = "Does the candidate match the criteria pass?";
    public static final int MAX_SCORE_VALUE = 10;
    public static final UserScoringCriteria USER_SCORING_CRITERIA_BAD = new UserScoringCriteria(USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS, CRITERIA_DESCRIPTION, true, MAX_SCORE_VALUE, CRITERIA_REQUEST_PASS);
    public static final UserScoringCriteria USER_SCORING_CRITERIA_GOOD = new UserScoringCriteria(USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS, CRITERIA_DESCRIPTION, false, MAX_SCORE_VALUE, CRITERIA_REQUEST_SCORE);
    public static final List<UserScoringCriteria> USER_SCORING_CRITERIA_LIST = List.of(USER_SCORING_CRITERIA_GOOD, USER_SCORING_CRITERIA_BAD);
    public static final ScoredCriteria SCORED_CRITERIA_GOOD = new ScoredCriteria(USER_CRITERIA_ID, CRITERIA_DESCRIPTION, JUSTIFICATION_POSITIVE, SCORE_VALUE_GOOD, MAX_SCORE_VALUE);
    public static final ScoredCriteria SCORED_CRITERIA_BAD = new ScoredCriteria(USER_CRITERIA_ID, CRITERIA_DESCRIPTION, JUSTIFICATION_NEGATIVE, SCORE_VALUE_BAD, MAX_SCORE_VALUE);
    public static final List<ScoredCriteria> SCORED_CRITERIA_LIST = List.of(SCORED_CRITERIA_GOOD, SCORED_CRITERIA_BAD);
    public static final CandidateScores CANDIDATE_SCORES = new CandidateScores(ANALYSIS_ID, CANDIDATE_NAME, SCORED_CRITERIA_LIST);
    public static final long USER_ID = 5345342L;
    public static final long CV_ID = 5634253242L;
    public static final long ROLE_ID = 5345654342L;
    public static final String CV_STRING_CONDENSED = "Condensed Cv String";
    public static final String CV_STRING_FULL = "Full Cv String "; //Multipart file adds that space by default
    public static final String CV_STRING_FULL_FOR_CONVERSION = "Full Cv String";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final GrantedAuthority GRANTED_AUTHORITY_USER = () -> "USER";
    public static final long CREDIT_AMOUNT = 543L;
    public static final int CREDIT_CHANGE_AMOUNT = 43;
    public static final long CANDIDATE_ID = 654432L;
    public static final CandidateData CANDIDATE_DATA = new CandidateData(CANDIDATE_ID, CANDIDATE_NAME, USER_ID, ROLE_ID, true);
    public static final CandidateData CANDIDATE_DATA_UNSELECTED = new CandidateData(CANDIDATE_ID, CANDIDATE_NAME, USER_ID, ROLE_ID, false);
    public static final List<CandidateData> CANDIDATE_DATA_LIST = List.of(CANDIDATE_DATA);
    public static final int SELECTED_CANDIDATE_COUNT = 453;
    public static final String HASH = "12055492ce597b78066c49ec03405635";
    public static final ProcessedCv PROCESSED_CV = new ProcessedCv(CV_ID, CANDIDATE_NAME, true, HASH, CV_STRING_CONDENSED);
    public static final List<ProcessedCv> PROCESSED_CV_LIST = List.of(PROCESSED_CV);
    public static final CvData CV_DATA = new CvData(CV_ID, HASH, CV_STRING_CONDENSED);
    public static final long DEFINED_SCORING_CRITERIA_ID_SCORE = 43543L;
    public static final long DEFINED_SCORING_CRITERIA_ID_PASS = 487673L;
    public static final String DEFINED_SCORING_CRITERIA_DESCRIPTION = "Description";
    public static final String INPUT_EXAMPLE = "Input Example";
    public static final String TOOLTIP = "Tooltip";
    public static final DefinedScoringCriteria DEFINED_SCORING_CRITERIA_PASS = new DefinedScoringCriteria(DEFINED_SCORING_CRITERIA_ID_PASS, DEFINED_SCORING_CRITERIA_DESCRIPTION, CriteriaCategory.HARD_SKILLS, false, Optional.empty(), CRITERIA_REQUEST_PASS, TOOLTIP);
    public static final DefinedScoringCriteria DEFINED_SCORING_CRITERIA_SCORE = new DefinedScoringCriteria(DEFINED_SCORING_CRITERIA_ID_SCORE, DEFINED_SCORING_CRITERIA_DESCRIPTION, CriteriaCategory.SOFT_SKILLS, true, Optional.of(INPUT_EXAMPLE), CRITERIA_REQUEST_SCORE, TOOLTIP);
    public static final List<DefinedScoringCriteria> DEFINED_SCORING_CRITERIA_LIST = List.of(DEFINED_SCORING_CRITERIA_SCORE, DEFINED_SCORING_CRITERIA_PASS);
    public static final String POSITION = "Position";
    public static final RoleDisplay ROLE_DISPLAY = new RoleDisplay(ROLE_ID, POSITION);
    public static final List<RoleDisplay> ROLE_DISPLAY_LIST = List.of(ROLE_DISPLAY);
    public static final int ROLE_CRITERIA_COUNT = 5;
    public static final String VALUE = "Java";
    public static final UserCriteria USER_CRITERIA = new UserCriteria(USER_CRITERIA_ID, DEFINED_SCORING_CRITERIA_ID_SCORE, Optional.of(VALUE), MAX_SCORE_VALUE);
    public static final UserCriteria USER_CRITERIA_WITHOUT_VALUE = new UserCriteria(USER_CRITERIA_ID, DEFINED_SCORING_CRITERIA_ID_SCORE, Optional.empty(), MAX_SCORE_VALUE);
    public static final Role ROLE = new Role(ROLE_ID, POSITION, USER_SCORING_CRITERIA_LIST);
    public static final Role ROLE_NEW = new Role(ROLE_ID, POSITION, Collections.emptyList());
    public static final org.smartjobs.adaptors.data.repository.data.Role DATABASE_ROLE = new org.smartjobs.adaptors.data.repository.data.Role(ROLE_ID, USER_ID, POSITION);
    public static final List<org.smartjobs.adaptors.data.repository.data.Role> DATABASE_ROLE_LIST = List.of(DATABASE_ROLE);
    public static final long SELECTED_ROLE_ID = 4346453L;
    public static final SelectedRole SELECTED_ROLE = new SelectedRole(SELECTED_ROLE_ID, USER_ID, ROLE_ID);
    public static final long ZERO = 0L;
    public static final Date NOW =  Date.valueOf(LocalDate.now());

    //PORT MOCKS
    public static AiService aiServiceMock() {
        AiService aiService = mock(AiService.class);
        when(aiService.extractCandidateName(CV_STRING_FULL)).thenReturn(Optional.of(CANDIDATE_NAME));
        when(aiService.scoreForCriteria(CV_STRING_CONDENSED, CRITERIA_REQUEST_SCORE, MAX_SCORE_VALUE)).thenReturn(Optional.of(SCORE_GOOD));
        when(aiService.passForCriteria(CV_STRING_CONDENSED, CRITERIA_REQUEST_PASS, MAX_SCORE_VALUE)).thenReturn(Optional.of(SCORE_BAD));
        when(aiService.anonymizeCv(CV_STRING_FULL)).thenReturn(Optional.of(CV_STRING_CONDENSED));
        return aiService;
    }

    public static AnalysisDal analysisDalMock() {
        ArgumentCaptor<List<ScoredCriteria>> scoredCriteriaCaptor = ArgumentCaptor.forClass(List.class);
        AnalysisDal analysisDal = mock(AnalysisDal.class);
        when(analysisDal.getResultById(anyLong())).thenReturn(CANDIDATE_SCORES);
        when(analysisDal.saveResults(ArgumentMatchers.eq(USER_ID), ArgumentMatchers.eq(CV_ID), ArgumentMatchers.eq(ROLE_ID), scoredCriteriaCaptor.capture()))
                .thenAnswer(_ -> {
                    Set<ScoredCriteria> expected = new HashSet<>(SCORED_CRITERIA_LIST);
                    Set<ScoredCriteria> found = new HashSet<>(scoredCriteriaCaptor.getValue());
                    assertEquals(expected.size(), found.size());
                    SCORED_CRITERIA_LIST.forEach(sc -> assertFalse(found.add(sc)));
                    return ANALYSIS_ID;
                });
        return analysisDal;
    }

    public static CredentialDal credentialDalMock() {
        CredentialDal credentialDal = mock(CredentialDal.class);
        when(credentialDal.getUser(USERNAME)).thenReturn(Optional.of(new User(USERNAME, PASSWORD, USER_ID, List.of(GRANTED_AUTHORITY_USER))));
        return credentialDal;
    }

    public static CreditDal creditDalMock() {
        CreditDal creditDal = mock(CreditDal.class);
        when(creditDal.getUserCredits(USER_ID)).thenReturn(CREDIT_AMOUNT);
        doNothing().when(creditDal).event(USER_ID, CREDIT_CHANGE_AMOUNT, CreditType.CREDIT);
        return creditDal;
    }

    public static CvDal cvDalMock() {
        CvDal cvDal = mock(CvDal.class);
        when(cvDal.updateCurrentlySelectedById(CV_ID, true)).thenReturn(Optional.of(CANDIDATE_DATA));
        when(cvDal.updateCurrentlySelectedById(CV_ID, false)).thenReturn(Optional.of(CANDIDATE_DATA_UNSELECTED));
        when(cvDal.findSelectedCandidateCount(USER_ID, ROLE_ID)).thenReturn(SELECTED_CANDIDATE_COUNT);
        when(cvDal.getAllCandidates(USER_ID, ROLE_ID)).thenReturn(CANDIDATE_DATA_LIST);
        when(cvDal.getByCvId(CV_ID)).thenReturn(CANDIDATE_DATA_LIST);
        when(cvDal.getByHash(HASH)).thenReturn(Optional.empty());
        when(cvDal.knownHash(HASH)).thenReturn(false);
        when(cvDal.getAllSelected(USER_ID, ROLE_ID)).thenReturn(PROCESSED_CV_LIST);
        return cvDal;
    }

    public static RoleDal roleDalMock() {
        RoleDal roleDal = mock(RoleDal.class);
        when(roleDal.getRoleById(ROLE_ID)).thenReturn(ROLE);
        when(roleDal.getUserRoles(USER_ID)).thenReturn(ROLE_DISPLAY_LIST);
        when(roleDal.saveRole(USER_ID, POSITION)).thenReturn(ROLE_ID);
        when(roleDal.getCurrentlySelectedRoleByUserId(USER_ID)).thenReturn(Optional.of(ROLE_ID));
        when(roleDal.getCurrentlySelectedRole(USER_ID)).thenReturn(Optional.of(ROLE));
        when(roleDal.countCriteriaForRole(ROLE_ID)).thenReturn(ROLE_CRITERIA_COUNT);
        when(roleDal.createNewUserCriteriaForRole(DEFINED_SCORING_CRITERIA_ID_SCORE, ROLE_ID, VALUE, MAX_SCORE_VALUE)).thenReturn(USER_CRITERIA);
        when(roleDal.getAllDefinedScoringCriteria()).thenReturn(DEFINED_SCORING_CRITERIA_LIST);
        return roleDal;
    }

    //SERVICE OBJECTS

    public static EventEmitter eventEmitter() {
        return new EventEmitterImpl();
    }

    public static CreditService creditService() {
        return new CreditServiceImpl(creditDalMock(), eventEmitter());
    }

    public static FileHandler fileHandler() {
        return new FileHandlerImpl();
    }

    public static MultipartFile file() {
        return new MockMultipartFile("MultipartFileTestInput.txt",
                "MultipartFileTestInput.txt",
                "text/plain",
                CV_STRING_FULL_FOR_CONVERSION.getBytes(StandardCharsets.UTF_8));
    }
}
