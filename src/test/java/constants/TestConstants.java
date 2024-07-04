package constants;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.data.repository.data.SelectedRole;
import org.smartjobs.adaptors.service.ai.gpt.data.GptMessage;
import org.smartjobs.adaptors.service.ai.gpt.data.GptModel;
import org.smartjobs.adaptors.service.ai.gpt.data.GptRole;
import org.smartjobs.adaptors.service.ai.gpt.request.GptRequest;
import org.smartjobs.adaptors.service.ai.gpt.response.GptChoices;
import org.smartjobs.adaptors.service.ai.gpt.response.GptResponse;
import org.smartjobs.adaptors.service.ai.gpt.response.GptUsage;
import org.smartjobs.adaptors.view.web.controller.roles.RolesController;
import org.smartjobs.core.constants.CreditType;
import org.smartjobs.core.entities.*;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.implementation.EventEmitterImpl;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.ports.dal.*;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.analysis.AnalysisServiceImpl;
import org.smartjobs.core.service.candidate.CandidateServiceImpl;
import org.smartjobs.core.service.candidate.FileHandler;
import org.smartjobs.core.service.candidate.file.FileHandlerImpl;
import org.smartjobs.core.service.credit.CreditServiceImpl;
import org.smartjobs.core.service.role.RoleServiceImpl;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.smartjobs.core.service.user.CodeSupplier;
import org.smartjobs.core.service.user.UserService;
import org.smartjobs.core.service.user.validation.UserDto;
import org.springframework.cache.support.NoOpCache;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class TestConstants {

    public static final String CODE = "CODE";

    private TestConstants() {
        //Private construction to prevent instantiation.
    }

    public static final String CANDIDATE_NAME = "Joe Daunt";
    public static final String CANDIDATE_NAME2 = "James Mottram";
    public static final String CRITERIA_DESCRIPTION = "Criteria Description";
    public static final long ANALYSIS_ID = 43532L;
    public static final long ANALYSIS_ID2 = 653534L;
    public static final String JUSTIFICATION_POSITIVE = "The candidate meets the criteria";
    public static final String JUSTIFICATION_MEDIUM = "The candidate almost meets the criteria";
    public static final String JUSTIFICATION_NEGATIVE = "The candidate does not meet the criteria";
    public static final int SCORE_VALUE_GOOD = 7;
    public static final int SCORE_VALUE_MEDIUM = 5;
    public static final Score SCORE_GOOD = new Score(JUSTIFICATION_POSITIVE, SCORE_VALUE_GOOD);
    public static final Score SCORE_MEDIUM = new Score(JUSTIFICATION_MEDIUM, SCORE_VALUE_MEDIUM);
    public static final int SCORE_VALUE_BAD = 0;
    public static final Score SCORE_BAD = new Score(JUSTIFICATION_NEGATIVE, SCORE_VALUE_BAD);
    public static final long USER_CRITERIA_ID = 22342L;
    public static final long USER_CRITERIA_ID2 = 5435234L;
    public static final String CRITERIA_REQUEST_SCORE = "Does the candidate match the criteria score?";
    public static final String CRITERIA_REQUEST_PASS = "Does the candidate match the criteria pass?";
    public static final int MAX_SCORE_VALUE = 10;
    public static final UserScoringCriteria USER_SCORING_CRITERIA_BAD = new UserScoringCriteria(USER_CRITERIA_ID, CriteriaCategory.HARD_SKILLS, CRITERIA_DESCRIPTION, true, MAX_SCORE_VALUE, CRITERIA_REQUEST_PASS);
    public static final UserScoringCriteria USER_SCORING_CRITERIA_GOOD = new UserScoringCriteria(USER_CRITERIA_ID2, CriteriaCategory.HARD_SKILLS, CRITERIA_DESCRIPTION, false, MAX_SCORE_VALUE, CRITERIA_REQUEST_SCORE);
    public static final List<UserScoringCriteria> USER_SCORING_CRITERIA_LIST = List.of(USER_SCORING_CRITERIA_GOOD, USER_SCORING_CRITERIA_BAD);
    public static final List<UserScoringCriteria> USER_SCORING_CRITERIA_LIST2 = List.of(USER_SCORING_CRITERIA_GOOD, USER_SCORING_CRITERIA_BAD);
    public static final ScoredCriteria SCORED_CRITERIA_GOOD = new ScoredCriteria(USER_CRITERIA_ID2, CRITERIA_DESCRIPTION, JUSTIFICATION_POSITIVE, SCORE_VALUE_GOOD, MAX_SCORE_VALUE);
    public static final ScoredCriteria SCORED_CRITERIA_MEDIUM = new ScoredCriteria(USER_CRITERIA_ID2, CRITERIA_DESCRIPTION, JUSTIFICATION_MEDIUM, SCORE_VALUE_MEDIUM, MAX_SCORE_VALUE);
    public static final ScoredCriteria SCORED_CRITERIA_BAD = new ScoredCriteria(USER_CRITERIA_ID, CRITERIA_DESCRIPTION, JUSTIFICATION_NEGATIVE, SCORE_VALUE_BAD, MAX_SCORE_VALUE);
    public static final List<ScoredCriteria> SCORED_CRITERIA_LIST = List.of(SCORED_CRITERIA_GOOD, SCORED_CRITERIA_BAD);
    public static final List<ScoredCriteria> SCORED_CRITERIA_LIST2 = List.of(SCORED_CRITERIA_MEDIUM, SCORED_CRITERIA_BAD);
    public static final CandidateScores CANDIDATE_SCORES = new CandidateScores(ANALYSIS_ID, CANDIDATE_NAME, SCORED_CRITERIA_LIST);
    public static final CandidateScores CANDIDATE_SCORES2 = new CandidateScores(ANALYSIS_ID2, CANDIDATE_NAME2, SCORED_CRITERIA_LIST2);
    public static final long USER_ID = 5345342L;
    public static final long USER_ID2 = 63454353L;
    public static final long CV_ID = 5634253242L;
    public static final long CV_ID2 = 5634253242L;
    public static final long ROLE_ID = 5345654342L;
    public static final long ROLE_ID2 = 64345L;
    public static final String CV_STRING_CONDENSED = "Condensed Cv String";
    public static final String CV_STRING_CONDENSED2 = "Condensed Cv String2";
    public static final String CV_STRING_FULL = "Full Cv String "; //Multipart file adds that space by default
    public static final String CV_STRING_FULL_FOR_CONVERSION_TXT = "Full Cv String";
    public static final String USERNAME = "email@email.com";
    public static final String USERNAME2 = "email2@email.com";
    public static final String USERNAME3 = "email3@email.com";
    public static final String PASSWORD = "Password1";

    public static final String AUTHORITY = "USER";
    public static final String PASSWORD2 = "Password2";
    public static final GrantedAuthority GRANTED_AUTHORITY_USER = () -> "USER";
    public static final long CREDIT_AMOUNT = 543L;
    public static final int CREDIT_CHANGE_AMOUNT = 43;
    public static final long CANDIDATE_ID = 654432L;
    public static final long CANDIDATE_ID2 = 643532L;
    public static final CandidateData CANDIDATE_DATA = new CandidateData(CANDIDATE_ID, CANDIDATE_NAME, USER_ID, ROLE_ID, true);
    public static final CandidateData CANDIDATE_DATA2 = new CandidateData(CANDIDATE_ID2, CANDIDATE_NAME2, USER_ID, ROLE_ID, true);
    public static final CandidateData CANDIDATE_DATA_UNSELECTED = new CandidateData(CANDIDATE_ID, CANDIDATE_NAME, USER_ID, ROLE_ID, false);
    public static final List<CandidateData> CANDIDATE_DATA_LIST = List.of(CANDIDATE_DATA, CANDIDATE_DATA2);
    public static final List<CandidateData> SINGLE_CANDIDATE_DATA_LIST = List.of(CANDIDATE_DATA);
    public static final int SELECTED_CANDIDATE_COUNT = 453;
    public static final String HASH_TXT = "12055492ce597b78066c49ec03405635";
    public static final ProcessedCv PROCESSED_CV = new ProcessedCv(CV_ID, CANDIDATE_NAME, true, HASH_TXT, CV_STRING_CONDENSED);
    public static final ProcessedCv PROCESSED_CV_INVALID = new ProcessedCv(0L, "", true, HASH_TXT, "");
    public static final ProcessedCv PROCESSED_CV2 = new ProcessedCv(CV_ID2, CANDIDATE_NAME2, true, HASH_TXT, CV_STRING_CONDENSED2);
    public static final List<ProcessedCv> PROCESSED_CV_LIST = List.of(PROCESSED_CV2, PROCESSED_CV);
    public static final CvData CV_DATA = new CvData(CV_ID, HASH_TXT, CV_STRING_CONDENSED);
    public static final long DEFINED_SCORING_CRITERIA_ID_SCORE = 43543L;
    public static final long DEFINED_SCORING_CRITERIA_ID_PASS = 487673L;
    public static final String DEFINED_SCORING_CRITERIA_DESCRIPTION = "Description";
    public static final String INPUT_EXAMPLE = "Input Example";
    public static final String TOOLTIP = "Tooltip";
    public static final DefinedScoringCriteria DEFINED_SCORING_CRITERIA_PASS = new DefinedScoringCriteria(DEFINED_SCORING_CRITERIA_ID_PASS, DEFINED_SCORING_CRITERIA_DESCRIPTION, CriteriaCategory.HARD_SKILLS, false, Optional.empty(), CRITERIA_REQUEST_PASS, TOOLTIP);
    public static final DefinedScoringCriteria DEFINED_SCORING_CRITERIA_SCORE = new DefinedScoringCriteria(DEFINED_SCORING_CRITERIA_ID_SCORE, DEFINED_SCORING_CRITERIA_DESCRIPTION, CriteriaCategory.SOFT_SKILLS, true, Optional.of(INPUT_EXAMPLE), CRITERIA_REQUEST_SCORE, TOOLTIP);
    public static final List<DefinedScoringCriteria> DEFINED_SCORING_CRITERIA_LIST = List.of(DEFINED_SCORING_CRITERIA_SCORE, DEFINED_SCORING_CRITERIA_PASS);
    public static final String POSITION = "Position";
    public static final String POSITION2 = "Position2";
    public static final RoleDisplay ROLE_DISPLAY = new RoleDisplay(ROLE_ID, POSITION);
    public static final RoleDisplay ROLE_DISPLAY2 = new RoleDisplay(ROLE_ID2, POSITION2);
    public static final List<RoleDisplay> ROLE_DISPLAY_LIST = List.of(ROLE_DISPLAY, ROLE_DISPLAY2);
    public static final int ROLE_CRITERIA_COUNT = 5;
    public static final String VALUE = "Java";
    public static final UserCriteria USER_CRITERIA = new UserCriteria(USER_CRITERIA_ID, DEFINED_SCORING_CRITERIA_ID_SCORE, Optional.of(VALUE), MAX_SCORE_VALUE);
    public static final UserCriteria USER_CRITERIA_WITHOUT_VALUE = new UserCriteria(USER_CRITERIA_ID, DEFINED_SCORING_CRITERIA_ID_SCORE, Optional.empty(), MAX_SCORE_VALUE);
    public static final Role ROLE = new Role(ROLE_ID, POSITION, USER_SCORING_CRITERIA_LIST);
    public static final Role ROLE2 = new Role(ROLE_ID2, POSITION2, USER_SCORING_CRITERIA_LIST2);
    public static final Role ROLE_NEW = new Role(ROLE_ID, POSITION, Collections.emptyList());
    public static final org.smartjobs.adaptors.data.repository.data.Role DATABASE_ROLE = new org.smartjobs.adaptors.data.repository.data.Role(ROLE_ID, USER_ID, POSITION);
    public static final org.smartjobs.adaptors.data.repository.data.Role DATABASE_ROLE2 = new org.smartjobs.adaptors.data.repository.data.Role(ROLE_ID2, USER_ID, POSITION2);
    public static final List<org.smartjobs.adaptors.data.repository.data.Role> DATABASE_ROLE_LIST = List.of(DATABASE_ROLE, DATABASE_ROLE2);
    public static final long SELECTED_ROLE_ID = 4346453L;
    public static final SelectedRole SELECTED_ROLE = new SelectedRole(SELECTED_ROLE_ID, USER_ID, ROLE_ID);
    public static final long ZERO = 0L;
    public static final Date NOW = Date.valueOf(LocalDate.now());
    public static final GptRequest GPT_REQUEST = new GptRequest(GptModel.GPT_3_5, 1.0, 0.5, List.of(new GptMessage(GptRole.SYSTEM, "Are you sentient yet")));
    public static final String GPT_RESPONSE_ID = "ID";
    public static final String GPT_RESPONSE_OBJECT = "OBJECT";
    public static final long GPT_RESPONSE_CREATED_TIME = 1234L;
    public static final GptUsage USAGE = new GptUsage(100, 50, 150);
    public static final GptResponse GPT_RESPONSE = new GptResponse(GPT_RESPONSE_ID, GPT_RESPONSE_OBJECT, GPT_RESPONSE_CREATED_TIME, GptModel.GPT_3_5, USAGE, List.of(new GptChoices(new GptMessage(GptRole.SYSTEM, "Not yet"), "Log", "End", 0)));
    public static final int MAX_CLIENT_RETRIES = 3;
    public static final int INITIAL_TIMEOUT_SECONDS = 1;
    public static final int USER_BASE_SCORE = 10;
    public static final String NAME_IDENTIFY_SYSTEM_PROMPT = """
            You are an expert in identifying names in CV documents.
            Scan the document to reliably find and return only the candidate's name, with proper casing.
            Do not include any additional information.""";
    public static final String CV_SHORT = "CV_DATA";
    public static final String CV_LONG = "CV_DATA_EXTENDED";
    public static final int CV_NAME_CHUNK = 500;

    public static final UserDto USER_DTO_EMPTY = new UserDto("", "", "");
    public static final UserDto USER_DTO_NEW = new UserDto(USERNAME3, PASSWORD2, PASSWORD2);
    public static final String ANON_CV_SYSTEM_PROMPT = """
            You are an expert in information extraction.
            Summarize the candidate's CV, including work history, certifications, skills, condensed job roles, and self-description.
            Ensure the summary is easy for an AI model to read, not necessarily a human.
            Remove unnecessary details and personal information such as name, age, sex, sexual orientation, and ethnicity, replacing them with neutral terms if needed.
            Maintain the length of employment and specific skills mentioned.
            Output only the summary, no additional commentary.
            Limit the output to a maximum of 600 tokens.""";

    public static final String SCORE_CV_SYSTEM_PROMPT = """
            Master of scoring candidates.
            Given scoring criteria and points, examine the provided CV.
            Determine match with criteria.
            Output max two sentences explaining the reason for the score, focused on the criteria match, without mentioning the score number.
            State "SCORE" then the score out of 10.
            Only award points for mentioned criteria factors.

            Example output:

            The candidate has held several positions where they have used Java, but shows no evidence of having used Python. SCORE 6
            The candidate does not have an advanced degree in Computer Science. SCORE 0
            The candidate fully meets the supplied criteria. SCORE 10""";


    public static final String PASS_CV_SYSTEM_PROMPT = """
            Given criteria, determine if the candidate meets it (true/false). Examine the CV to assess criteria match.

            Output two sentences max:

            State if the candidate meets the criteria.
            Follow with "PASS true/false".
            If no match, return false.

            Examples:

            The candidate does not have an advanced degree in Computer Science. PASS false
            The candidate has an AWS certification. PASS true""";

    public static final String ANON_CV = "ANON_CV";
    public static final ConcurrentModel MODEL = new ConcurrentModel();
    public static final User USER = new User(USERNAME, PASSWORD, USER_ID, List.of(GRANTED_AUTHORITY_USER));
    public static final User USER2 = new User(USERNAME2, PASSWORD2, USER_ID2, List.of(GRANTED_AUTHORITY_USER));
    public static final RolesController.Category SOFT_SKILLS = new RolesController.Category("Soft Skills", Collections.emptyList());
    public static final RolesController.Category RELEVANT_EXPERIENCE = new RolesController.Category("Relevant Experience", Collections.emptyList());
    public static final RolesController.Category QUALIFICATIONS = new RolesController.Category("Qualifications", Collections.emptyList());
    public static final RolesController.Category PROFESSIONAL_ENGAGEMENT_AND_RECOGNITION = new RolesController.Category("Professional Engagement and Recognition", Collections.emptyList());
    public static final RolesController.ScoringCriteria DISPLAY_SCORING_CRITERIA1 = new RolesController.ScoringCriteria(USER_CRITERIA_ID2, CRITERIA_DESCRIPTION, MAX_SCORE_VALUE);
    public static final RolesController.ScoringCriteria DISPLAY_SCORING_CRITERIA2 = new RolesController.ScoringCriteria(USER_CRITERIA_ID, CRITERIA_DESCRIPTION, MAX_SCORE_VALUE);
    public static final RolesController.Category HARD_SKILLS = new RolesController.Category("Hard Skills", List.of(DISPLAY_SCORING_CRITERIA1, DISPLAY_SCORING_CRITERIA2));
    public static final RolesController.Category HARD_SKILLS_EMPTY = new RolesController.Category("Hard Skills", Collections.emptyList());
    public static final List<RolesController.Category> CATEGORY_LIST = List.of(SOFT_SKILLS, HARD_SKILLS, RELEVANT_EXPERIENCE, QUALIFICATIONS, PROFESSIONAL_ENGAGEMENT_AND_RECOGNITION);
    public static final List<RolesController.Category> CATEGORY_LIST_NEW = List.of(SOFT_SKILLS, HARD_SKILLS_EMPTY, RELEVANT_EXPERIENCE, QUALIFICATIONS, PROFESSIONAL_ENGAGEMENT_AND_RECOGNITION);
    public static final RolesController.Role DISPLAY_ROLE = new RolesController.Role(ROLE_ID, POSITION, CATEGORY_LIST);
    public static final RolesController.Role DISPLAY_ROLE_NEW = new RolesController.Role(ROLE_ID, POSITION, CATEGORY_LIST_NEW);
    public static final List<String> CATEGORY_STRINGS = List.of("Hard Skills", "Professional Engagement and Recognition", "Qualifications", "Relevant Experience", "Soft Skills");
    public static final AiService AI_SERVICE = aiServiceMock();
    public static final CvDal CV_DAL = cvDalMock();
    public static final EventEmitter EVENT_EMITTER = new EventEmitterImpl();
    public static final FileHandler FILE_HANDLER = new FileHandlerImpl();
    public static final RoleDal ROLE_DAL = roleDalMock();
    public static final AnalysisDal ANALYSIS_DAL = analysisDalMock();
    public static final CreditDal CREDIT_DAL = creditDalMock();
    public static final CreditService CREDIT_SERVICE = new CreditServiceImpl(CREDIT_DAL, EVENT_EMITTER);
    public static final CandidateServiceImpl CANDIDATE_SERVICE = new CandidateServiceImpl(AI_SERVICE, CV_DAL, EVENT_EMITTER, CREDIT_SERVICE, FILE_HANDLER);
    public static final int MAX_ALLOWED_CRITERIA = 10;
    public static final RoleServiceImpl ROLE_SERVICE = new RoleServiceImpl(ROLE_DAL, MAX_ALLOWED_CRITERIA);
    public static final AnalysisServiceImpl ANALYSIS_SERVICE = new AnalysisServiceImpl(AI_SERVICE, EVENT_EMITTER, CREDIT_SERVICE, ANALYSIS_DAL, ROLE_CRITERIA_COUNT);
    public static final CredentialDal CREDENTIAL_DAL = credentialDalMock();
    public static final PasswordEncoder PASSWORD_ENCODER = passwordEncoder();
    public static final Validator VALIDATOR = validator();
    public static final CodeSupplier CODE_SUPPLIER = () -> CODE;
    public static final NoOpCache EMAIL_VALIDATION_CACHE = new NoOpCache("email-validation");
    public static final UserService USER_SERVICE = new UserService(CREDENTIAL_DAL, PASSWORD_ENCODER, VALIDATOR, EVENT_EMITTER, CODE_SUPPLIER, EMAIL_VALIDATION_CACHE);


    //PORT MOCKS
    public static AiService aiServiceMock() {
        AiService aiService = mock(AiService.class);
        when(aiService.extractCandidateName(CV_STRING_FULL)).thenReturn(Optional.of(CANDIDATE_NAME));
        when(aiService.scoreForCriteria(CV_STRING_CONDENSED, CRITERIA_REQUEST_SCORE, MAX_SCORE_VALUE)).thenReturn(Optional.of(SCORE_GOOD));
        when(aiService.scoreForCriteria(CV_STRING_CONDENSED2, CRITERIA_REQUEST_SCORE, MAX_SCORE_VALUE)).thenReturn(Optional.of(SCORE_MEDIUM));
        when(aiService.passForCriteria(CV_STRING_CONDENSED, CRITERIA_REQUEST_PASS, MAX_SCORE_VALUE)).thenReturn(Optional.of(SCORE_BAD));
        when(aiService.passForCriteria(CV_STRING_CONDENSED2, CRITERIA_REQUEST_PASS, MAX_SCORE_VALUE)).thenReturn(Optional.of(SCORE_BAD));
        when(aiService.anonymizeCv(CV_STRING_FULL)).thenReturn(Optional.of(CV_STRING_CONDENSED));
        return aiService;
    }

    public static AnalysisDal analysisDalMock() {
        ArgumentCaptor<List<ScoredCriteria>> scoredCriteriaCaptor = ArgumentCaptor.forClass(List.class);
        AnalysisDal analysisDal = mock(AnalysisDal.class);
        when(analysisDal.getResultById(ANALYSIS_ID)).thenReturn(CANDIDATE_SCORES);
        when(analysisDal.getResultById(ANALYSIS_ID2)).thenReturn(CANDIDATE_SCORES2);
        when(analysisDal.saveResults(USER_ID, CV_ID, ROLE_ID, SCORED_CRITERIA_LIST)).thenReturn(ANALYSIS_ID);
        when(analysisDal.saveResults(USER_ID, CV_ID, ROLE_ID, SCORED_CRITERIA_LIST2)).thenReturn(ANALYSIS_ID2);
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
        when(cvDal.updateCurrentlySelectedAll(USER_ID, ROLE_ID, true)).thenReturn(CANDIDATE_DATA_LIST);
        when(cvDal.findSelectedCandidateCount(USER_ID, ROLE_ID)).thenReturn(SELECTED_CANDIDATE_COUNT);
        when(cvDal.getAllCandidates(USER_ID, ROLE_ID)).thenReturn(CANDIDATE_DATA_LIST);
        when(cvDal.getByCvId(CV_ID)).thenReturn(SINGLE_CANDIDATE_DATA_LIST);
        when(cvDal.getByHash(HASH_TXT)).thenReturn(Optional.empty());
        when(cvDal.knownHash(HASH_TXT)).thenReturn(false);
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

    public static PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.equals(encodedPassword);
            }
        };
    }

    public static Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    //SERVICE OBJECTS
    public static MultipartFile fileTxt() {
        return new MockMultipartFile("MultipartFileTestInput.txt",
                "MultipartFileTestInput.txt",
                "text/plain",
                CV_STRING_FULL_FOR_CONVERSION_TXT.getBytes(StandardCharsets.UTF_8));
    }

    public static MultipartFile fileDocFailing() {
        return new MockMultipartFile("MultipartFileTestInput.doc",
                "MultipartFileTestInput.doc",
                "application/msword",
                "".getBytes(StandardCharsets.UTF_8));
    }

    public static MultipartFile filePdfFailing() {
        return new MockMultipartFile("MultipartFileTestInput.pdf",
                "MultipartFileTestInput.pfd",
                MediaType.APPLICATION_PDF_VALUE,
                "".getBytes(StandardCharsets.UTF_8));
    }

    public static MultipartFile fileWithoutAName() {
        return new MockMultipartFile("No Name",
                null,
                "text/plain",
                CV_STRING_FULL_FOR_CONVERSION_TXT.getBytes(StandardCharsets.UTF_8));
    }

    public static MockHttpServletResponse mockHttpServletResponse() {
        return new MockHttpServletResponse();
    }

    public static SecureRandom mockSecureRandom() {
        SecureRandom secureRandom = mock(SecureRandom.class);
        when(secureRandom.nextInt(1_000_000)).thenReturn(12);
        return secureRandom;
    }
}