package org.smartjobs.com.client.gpt;

import com.google.gson.Gson;
import org.smartjobs.com.client.gpt.request.GptRequest;
import org.smartjobs.com.client.gpt.response.GptResponse;
import org.smartjobs.com.client.gpt.response.GptUserExtraction;
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

import static org.smartjobs.com.client.gpt.request.GptRequest.*;

@Component
public class GptClient {

    private final HttpClient client;
    private final Gson gson;
    private final URI clientUri;

    @Value("${gpt.api.key}")
    private String apiKey;

    @Autowired
    public GptClient(HttpClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
        try {
            this.clientUri = new URI("https://api.openai.com/v1/chat/completions");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String justifyDecision(int match, String candidateCv, String jobListing) {
        GptRequest gptRequest = justifyGptDecision(match, candidateCv, jobListing);
        GptResponse response = sendMessage(gptRequest);
        System.out.println(response);
        return response.choices().stream().map(choice -> choice.message().content()).collect(Collectors.joining("\n"));
    }

    public GptUserExtraction parseCandidateData(String cvData) {
        GptRequest gptRequest = informationExtractionRequest(cvData);
        GptResponse response = sendMessage(gptRequest);
        System.out.println(response);
        return response.choices().stream().map(choice -> choice.message().content()).findFirst().map(json -> gson.fromJson(json, GptUserExtraction.class)).orElseThrow();
    }

    public int determineMatch(String listingDescription, String candidateDescription) {
        GptRequest gptRequest = evaluateCandidate(listingDescription, candidateDescription);
        GptResponse response = sendMessage(gptRequest);
        return response.choices().stream().map(choice -> choice.message().content()).findFirst().map(Integer::parseInt).orElseThrow();

    }

    private GptResponse sendMessage(GptRequest request) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(clientUri)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
            return gson.fromJson(response.body(), GptResponse.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
