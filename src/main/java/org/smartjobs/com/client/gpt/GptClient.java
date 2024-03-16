package org.smartjobs.com.client.gpt;

import com.google.gson.Gson;
import org.smartjobs.com.client.gpt.request.GptRequest;
import org.smartjobs.com.client.gpt.response.GptResponse;
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

import static org.smartjobs.com.client.gpt.data.GptMessage.systemMessage;
import static org.smartjobs.com.client.gpt.data.GptMessage.userMessage;
import static org.smartjobs.com.client.gpt.request.GptRequest.gpt3;

@Component
public class GptClient {

    private final HttpClient client;
    private final Gson gson;
    private final URI clientUri;

    @Value("${gpt.api.key}")
    private String apiKey;

    @Value("${gpt.api.url}")
    private String gptUrl;

    @Autowired
    public GptClient(HttpClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
        try {
            this.clientUri = new URI(gptUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String evaluateCv(String cv) {
        GptRequest gptRequest = gpt3(
                systemMessage("You are in charge of evaluating cvs. Please say if this cv is good or not"),
                userMessage(cv));
        GptResponse response = sendMessage(gptRequest);
        return response.choices().stream().map(choice -> choice.message().content()).collect(Collectors.joining());
    }

    public String condenseCvData(String cvData) {
        GptRequest gptRequest = gpt3(
                systemMessage("You are an expert in finding the best way to extract and condense the most relevant information out of text. The text that you generate doesn't have to be easy for a human to read. It has to be optimized for chat gpt to extract the most information out of it as possible. I will give you some cv text and I want you to extract out only the most important data about this individual. Please remove any information about the users name, age, their sex or their ethnicity"),
                userMessage(cvData));
        GptResponse response = sendMessage(gptRequest);
        return response.choices().stream().map(choice -> choice.message().content()).collect(Collectors.joining());
    }

    public int determineMatch(String listingDescription, String text) {
        GptRequest gptRequest = gpt3(
                systemMessage("You are an expert in evaluating candidates and finding which one is the best match for a job description. I want you to consider the provided listing and the candidate, and then tell me a number of how well the candidate matches the job on a scale of 1 to 100. Only return the number, no other information. Just return the number"),
                userMessage("Job listing: " + listingDescription + " Candidate Information: " + text));
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
