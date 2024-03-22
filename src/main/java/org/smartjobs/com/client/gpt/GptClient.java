package org.smartjobs.com.client.gpt;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.client.gpt.request.GptRequest;
import org.smartjobs.com.client.gpt.response.GptResponse;
import org.smartjobs.com.client.gpt.response.GptUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

import static org.smartjobs.com.client.gpt.request.GptRequest.evaluateCandidate;
import static org.smartjobs.com.client.gpt.request.GptRequest.justifyGptDecision;

@Component
public class GptClient {

    private static final Logger logger = LoggerFactory.getLogger(GptClient.class);

    private final HttpClient client;
    private final Gson gson;
    private final URI clientUri;
    private final String apiKey;

    @Autowired
    public GptClient(
            HttpClient client,
            Gson gson,
            @Value("${gpt.api.url}") String url,
            @Value("${gpt.api.key}") String apiKey) {
        this.client = client;
        this.gson = gson;
        try {
            this.clientUri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.apiKey = apiKey;
    }

    public String justifyDecision(int match, String candidateCv, String jobListing) {
        GptRequest gptRequest = justifyGptDecision(match, candidateCv, jobListing);
        GptResponse response = sendMessage(gptRequest);
        return response.choices().stream()
                .map(choice -> choice.message().content())
                .collect(Collectors.joining("\n"));
    }

    public String extractCandidateName(String cvData) {
        GptRequest gptRequest = GptRequest.extractCandidateName(cvData);
        GptResponse response = sendMessage(gptRequest);
        return response.choices().stream()
                .map(choice -> choice.message().content())
                .collect(Collectors.joining("\n"));
    }

    public String anonymousCandidateDescription(String cvData) {
        GptRequest gptRequest = GptRequest.anonymousCandidateDescription(cvData);
        GptResponse response = sendMessage(gptRequest);
        return response.choices().stream()
                .map(choice -> choice.message().content())
                .collect(Collectors.joining("\n"));

    }

    public int determineMatchPercentage(String listingDescription, String candidateDescription) {
        GptRequest gptRequest = evaluateCandidate(listingDescription, candidateDescription);
        GptResponse response = sendMessage(gptRequest);
        return response.choices().stream()
                .map(choice -> choice.message().content())
                .findFirst()
                .map(Integer::parseInt)
                .orElseThrow();

    }

    private GptResponse sendMessage(GptRequest request) {
        logger.trace("Calling GPT with request: {}", request);
        HttpRequest httpRequest = createRequest(request);
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
            GptResponse gptResponse = gson.fromJson(response.body(), GptResponse.class);
            logger.trace("Received response from GPT: {}", gptResponse);
            GptUsage usage = gptResponse.usage();
            logger.info("Token spent: Prompt {}, Completion, {} Total, {}", usage.promptTokens(), usage.completionTokens(), usage.totalTokens());
            return gptResponse;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest createRequest(GptRequest request) {
        return HttpRequest.newBuilder()
                .uri(clientUri)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();
    }
}
