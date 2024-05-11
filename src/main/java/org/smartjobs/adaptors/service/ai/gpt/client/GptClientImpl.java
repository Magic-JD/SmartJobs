package org.smartjobs.adaptors.service.ai.gpt.client;

import com.google.gson.Gson;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.service.ai.gpt.GptClient;
import org.smartjobs.adaptors.service.ai.gpt.exception.ClientExceptions.ServiceCallException;
import org.smartjobs.adaptors.service.ai.gpt.properties.GptProperties;
import org.smartjobs.adaptors.service.ai.gpt.request.GptRequest;
import org.smartjobs.adaptors.service.ai.gpt.response.GptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.ofSeconds;


@Component
@Slf4j
public class GptClientImpl implements GptClient {
    public static final String FAILED_GPT_CALL = "Failure calling GPT, code {}, body {}";

    private final HttpClient client;
    private final Gson gson;
    private final URI uri;
    private final String apiKey;

    private final Bucket bucket;
    private final int maxRetries;
    private final int initialTimeoutSeconds;

    @Autowired
    public GptClientImpl(HttpClient client, GptProperties properties, Gson gson, Bucket bucket) {
        this.client = client;
        this.uri = properties.getUri();
        this.apiKey = properties.getApiKey();
        this.gson = gson;
        this.maxRetries = properties.getMaxRetries();
        this.initialTimeoutSeconds = properties.getInitialTimeoutSeconds();
        this.bucket = bucket;
    }

    @Override
    public Optional<GptResponse> makeServiceCall(GptRequest request) {
        return makeServiceCall(request, 1);
    }

    public Optional<GptResponse> makeServiceCall(GptRequest gptRequest, int callNumber) {
        log.info("Preparing to call gpt when available.");
        bucket.asBlocking().consumeUninterruptibly(1); // Wait until we can safely call without triggering overloading.
        try {
            var request = createRequest(gptRequest, callNumber * initialTimeoutSeconds);
            HttpResponse<String> response = client.send(request, ofString());
            int statusCode = response.statusCode();
            if (statusCode == 200) { //Success response.
                return Optional.of(gson.fromJson(response.body(), GptResponse.class));
            }
            if (statusCode >= 429) { //Possible that re calling this code will get a success response
                throw new ServiceCallException(STR. "\{ statusCode } response from service." );
            } // Otherwise expect that we need to change something on our side before a success can be achieved.
            log.error(FAILED_GPT_CALL, statusCode, response.body());
            return Optional.empty();
        } catch (IOException | InterruptedException | ServiceCallException e) {
            log.error("Call {} failed to get a valid response from the server due to exception {}.", callNumber, e.getMessage());
            if (callNumber >= maxRetries) {
                log.error("After all retries, the request could not be processed.");
                log.trace("Failed error message: {}", gptRequest);
                return Optional.empty();
            }
            return makeServiceCall(gptRequest, callNumber + 1);
        }
    }

    private HttpRequest createRequest(GptRequest request, int timeoutDuration) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(ofSeconds(timeoutDuration))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();
    }
}
