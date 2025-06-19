package org.smartjobs.adaptors.service.ai;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.adaptors.service.ai.gpt.GptClient;
import org.smartjobs.adaptors.service.ai.gpt.data.GptMessage;
import org.smartjobs.adaptors.service.ai.gpt.data.GptModel;
import org.smartjobs.adaptors.service.ai.gpt.data.GptRole;
import org.smartjobs.adaptors.service.ai.gpt.request.GptRequest;
import org.smartjobs.adaptors.service.ai.gpt.response.GptChoices;
import org.smartjobs.adaptors.service.ai.gpt.response.GptResponse;
import org.smartjobs.adaptors.service.ai.gpt.response.ScoreParser;
import org.smartjobs.core.entities.Score;

import java.util.List;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class GptServiceTest {

    private final GptClient gptClient = mock(GptClient.class);
    private final GptService gptService = new GptService(gptClient, new ScoreParser(), USER_BASE_SCORE, CV_NAME_CHUNK);

    @Test
    void testExtractCandidateNameWillReturnTheNameProvidedInTheResponse() {
        GptRequest gptRequest = createGptRequest(NAME_IDENTIFY_SYSTEM_PROMPT, CV_SHORT);
        GptResponse gptResponse = createGptResponse(CANDIDATE_NAME);
        when(gptClient.makeServiceCall(gptRequest)).thenReturn(Optional.of(gptResponse));
        Optional<String> result = gptService.extractCandidateName(CV_SHORT);
        verify(gptClient).makeServiceCall(gptRequest);
        assertEquals(Optional.of(CANDIDATE_NAME), result);
    }

    @Test
    void testExtractCandidateNameWillOnlyUseTheGivenAmountOfCharactersFromTheCv() {
        GptService gptService = new GptService(gptClient, new ScoreParser(), USER_BASE_SCORE, CV_SHORT.length());
        GptRequest gptRequest = createGptRequest(NAME_IDENTIFY_SYSTEM_PROMPT, CV_SHORT);
        GptResponse gptResponse = createGptResponse(CANDIDATE_NAME);
        when(gptClient.makeServiceCall(gptRequest)).thenReturn(Optional.of(gptResponse));
        Optional<String> response = gptService.extractCandidateName(CV_LONG);
        verify(gptClient).makeServiceCall(gptRequest);
        assertEquals(Optional.of(CANDIDATE_NAME), response);
    }

    @Test
    void testAnonymizeCvUsesTheCorrectPromptsToGetAResponse() {
        GptRequest gptRequest = createGptRequest(ANON_CV_SYSTEM_PROMPT, CV_SHORT);
        GptResponse gptResponse = createGptResponse(ANON_CV);
        when(gptClient.makeServiceCall(gptRequest)).thenReturn(Optional.of(gptResponse));
        Optional<String> response = gptService.anonymizeCv(CV_SHORT);
        verify(gptClient).makeServiceCall(gptRequest);
        assertEquals(Optional.of(ANON_CV), response);
    }

    @Test
    void testScoreForCriteriaWillReturnTheCorrectScoresForTheGivenValues() {
        GptRequest gptRequest = createGptRequest(SCORE_CV_SYSTEM_PROMPT, "CV: CV_DATA. Scoring criteria: CRITERIA");
        GptResponse gptResponse = createGptResponse("Description. SCORE 10");
        when(gptClient.makeServiceCall(gptRequest)).thenReturn(Optional.of(gptResponse));
        Optional<Score> response = gptService.scoreForCriteria(CV_SHORT, "CRITERIA", 20);
        verify(gptClient).makeServiceCall(gptRequest);
        assertEquals(Optional.of(new Score("Description.", 20)), response);
    }

    @Test
    void testScoreForCriteriaWillChangeTheScoreValueDependingOnTheGivenMaxScore() {
        GptRequest gptRequest = createGptRequest(SCORE_CV_SYSTEM_PROMPT, "CV: CV_DATA. Scoring criteria: CRITERIA");
        GptResponse gptResponse = createGptResponse("Description. SCORE 5");
        when(gptClient.makeServiceCall(gptRequest)).thenReturn(Optional.of(gptResponse));
        assertEquals(Optional.of(new Score("Description.", 20)), gptService.scoreForCriteria(CV_SHORT, "CRITERIA", 40));
        assertEquals(Optional.of(new Score("Description.", 2.5)), gptService.scoreForCriteria(CV_SHORT, "CRITERIA", 5));
        assertEquals(Optional.of(new Score("Description.", 7.5)), gptService.scoreForCriteria(CV_SHORT, "CRITERIA", 15));
    }

    @Test
    void testPassForCriteriaWillReturnTheCorrectScoreFromTheSystemWhenPassIsTrue() {
        GptRequest gptRequest = createGptRequest(PASS_CV_SYSTEM_PROMPT, "CV: CV_DATA. Scoring criteria: CRITERIA");
        GptResponse gptResponse = createGptResponse("Description. PASS true");
        when(gptClient.makeServiceCall(gptRequest)).thenReturn(Optional.of(gptResponse));
        Optional<Score> response = gptService.passForCriteria(CV_SHORT, "CRITERIA", 20);
        verify(gptClient).makeServiceCall(gptRequest);
        assertEquals(Optional.of(new Score("Description.", 20)), response);
    }

    @Test
    void testPassForCriteriaWillReturnTheCorrectScoreFromTheSystemWhenPassIsFalse() {
        GptRequest gptRequest = createGptRequest(PASS_CV_SYSTEM_PROMPT, "CV: CV_DATA. Scoring criteria: CRITERIA");
        GptResponse gptResponse = createGptResponse("Description. PASS false");
        when(gptClient.makeServiceCall(gptRequest)).thenReturn(Optional.of(gptResponse));
        Optional<Score> response = gptService.passForCriteria(CV_SHORT, "CRITERIA", 20);
        verify(gptClient).makeServiceCall(gptRequest);
        assertEquals(Optional.of(new Score("Description.", 0)), response);
    }

    private GptResponse createGptResponse(String content) {
        return new GptResponse(GPT_RESPONSE_ID, GPT_RESPONSE_OBJECT, GPT_RESPONSE_CREATED_TIME, GptModel.GPT_3_5, USAGE, List.of(new GptChoices(new GptMessage(GptRole.SYSTEM, content), "Log", "End", 0)));
    }

    private GptRequest createGptRequest(String systemMessage, String userMessage) {
        return new GptRequest(GptModel.GPT_4_1_MINI, 0.0, 0.1, List.of(new GptMessage(GptRole.SYSTEM, systemMessage), new GptMessage(GptRole.USER, userMessage)));
    }
}