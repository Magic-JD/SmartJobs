package org.smartjobs.adaptors.service.ai.gpt.client;

import com.google.gson.Gson;
import display.CamelCaseDisplayNameGenerator;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.service.ai.gpt.GptClient;
import org.smartjobs.adaptors.service.ai.gpt.config.GptConfig;
import org.smartjobs.adaptors.service.ai.gpt.properties.GptProperties;
import org.smartjobs.adaptors.service.ai.gpt.response.GptResponse;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static constants.TestConstants.*;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class GptClientImplTest {

    private final HttpClient httpClient = mock(HttpClient.class);
    private final GptProperties gptProperties = new GptProperties("https://test.client.com", "API_KEY", MAX_CLIENT_RETRIES, INITIAL_TIMEOUT_SECONDS);
    private final GptConfig gptConfig = new GptConfig();
    private final Gson gson = gptConfig.gson();
    private final Bucket bucket = gptConfig.bucket(30_000);
    private final GptClient gptClient = new GptClientImpl(httpClient, gptProperties, gson, bucket);
    private final HttpResponse<String> passingResponse = mock(HttpResponse.class);
    private final HttpResponse<String> failingResponse500 = mock(HttpResponse.class);
    private final HttpResponse<String> failingResponse400 = mock(HttpResponse.class);

    {
        when(passingResponse.statusCode()).thenReturn(200);
        when(passingResponse.body()).thenReturn(gson.toJson(GPT_RESPONSE));
        when(failingResponse500.statusCode()).thenReturn(500);
        when(failingResponse400.statusCode()).thenReturn(400);
    }

    private ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);

    @Test
    void testMakeServiceCallWillReturnTheExpectedResponseWhenTheClientReturns() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), eq(ofString()))).thenReturn(passingResponse);
        assertEquals(Optional.of(GPT_RESPONSE), gptClient.makeServiceCall(GPT_REQUEST));
    }

    @Test
    void testMakeServiceCallWillReturnAnEmptyOptionalOnFailure() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), eq(ofString()))).thenReturn(failingResponse500);
        assertEquals(Optional.empty(), gptClient.makeServiceCall(GPT_REQUEST));
    }

    @Test
    void testMakeServiceCallWillTryTheRequiredAmountOfTimesOnFailureIfCodeIsHigherThanC429() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), eq(ofString()))).thenReturn(failingResponse500);
        gptClient.makeServiceCall(GPT_REQUEST);
        verify(httpClient, times(MAX_CLIENT_RETRIES)).send(any(), any());
    }

    @Test
    void testMakeServiceCallWillNotRetryIfTheErrorCodeIsLessThanC429() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), eq(ofString()))).thenReturn(failingResponse400);
        gptClient.makeServiceCall(GPT_REQUEST);
        verify(httpClient, times(INITIAL_TIMEOUT_SECONDS)).send(any(), any());
    }

    @Test
    void testTheRequestHasTheExpectedParameters() throws IOException, InterruptedException {
        when(httpClient.send(requestCaptor.capture(), eq(ofString()))).thenReturn(passingResponse);
        gptClient.makeServiceCall(GPT_REQUEST);
        HttpRequest request = requestCaptor.getValue();
        assertEquals(gptProperties.getUri(), request.uri());
        Map<String, List<String>> headers = request.headers().map();
        assertEquals(3, headers.size());
        assertEquals("application/json", headers.get("Accept").getFirst());
        assertEquals("application/json", headers.get("Content-Type").getFirst());
        assertEquals("Bearer API_KEY", headers.get("Authorization").getFirst());
        assertEquals(Optional.of(ofSeconds(INITIAL_TIMEOUT_SECONDS)), request.timeout());
    }

    @Test
    void testTheRequestTimeoutIncreasesAfterEachRequest() throws IOException, InterruptedException {
        when(httpClient.send(requestCaptor.capture(), eq(ofString()))).thenReturn(failingResponse500);
        gptClient.makeServiceCall(GPT_REQUEST);
        List<HttpRequest> allValues = requestCaptor.getAllValues();
        HttpRequest thirdRequest = allValues.removeLast();
        HttpRequest secondRequest = allValues.removeLast();
        HttpRequest firstRequest = allValues.removeLast();
        assertEquals(Optional.of(ofSeconds(INITIAL_TIMEOUT_SECONDS)), firstRequest.timeout());
        assertEquals(Optional.of(ofSeconds(INITIAL_TIMEOUT_SECONDS * 2)), secondRequest.timeout());
        assertEquals(Optional.of(ofSeconds(INITIAL_TIMEOUT_SECONDS * 3)), thirdRequest.timeout());
    }

    @Test
    void testAfterAFailedAttemptWithACodeMoreThanC429ACorrectValueShouldBeReturned() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), eq(ofString()))).thenReturn(failingResponse500, passingResponse);
        Optional<GptResponse> gptResponse = gptClient.makeServiceCall(GPT_REQUEST);
        verify(httpClient, times(2)).send(any(), any());
        assertEquals(Optional.of(GPT_RESPONSE), gptResponse);
    }

    @Test
    void testThatTheBucketWillPreventTooManyRequestsPerMinute() throws IOException, InterruptedException {
        when(httpClient.send(requestCaptor.capture(), eq(ofString()))).thenReturn(failingResponse500, passingResponse);
        Bucket bucket = mock(Bucket.class);
        BlockingBucket blockingBucket = mock(BlockingBucket.class);
        when(bucket.asBlocking()).thenReturn(blockingBucket);
        GptClient gptClient = new GptClientImpl(httpClient, gptProperties, gson, bucket);
        gptClient.makeServiceCall(GPT_REQUEST);
        verify(blockingBucket, times(2)).consumeUninterruptibly(1);
    }
}