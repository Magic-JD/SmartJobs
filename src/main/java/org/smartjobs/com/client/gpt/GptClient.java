package org.smartjobs.com.client.gpt;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.client.gpt.request.GptRequest;
import org.smartjobs.com.client.gpt.response.GptResponse;
import org.smartjobs.com.client.gpt.response.GptUsage;
import org.smartjobs.com.exception.categories.ApplicationExceptions.GptClientConnectionFailure;
import org.smartjobs.com.exception.categories.AsynchronousExceptions.JustificationException;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.smartjobs.com.client.gpt.request.GptRequest.evaluateCandidate;
import static org.smartjobs.com.client.gpt.request.GptRequest.justifyGptDecision;
import static org.smartjobs.com.concurrency.ConcurrencyUtil.virtualThreadList;

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
            throw new GptClientConnectionFailure(e);
        }
        this.apiKey = apiKey;
    }

    public String justifyDecision(int match, String candidateCv, String jobListing) {
        GptRequest gptRequest = justifyGptDecision(match, candidateCv, jobListing);
        var response = sendMessage(gptRequest);
        return response.map(rp -> rp.choices().stream()
                .map(choice -> choice.message().content())
                        .collect(Collectors.joining("\n")))
                .orElseThrow(JustificationException::new);
    }

    public Optional<String> extractCandidateName(String cvData) {
        GptRequest gptRequest = GptRequest.extractCandidateName(cvData);
        var response = sendMessage(gptRequest);
        return response.map(rp -> rp.choices().stream()
                .map(choice -> choice.message().content())
                .collect(Collectors.joining("\n")));
    }

    public Optional<String> anonymousCandidateDescription(String cvData) {
        GptRequest gptRequest = GptRequest.anonymousCandidateDescription(cvData);
        var response = sendMessage(gptRequest);
        return response.map(rp -> rp.choices().stream()
                .map(choice -> choice.message().content())
                .collect(Collectors.joining("\n")));

    }

    public int determineMatchPercentage(String listingDescription, String candidateDescription) {
        GptRequest gptRequest = evaluateCandidate(listingDescription, candidateDescription);
        var response = sendMessage(gptRequest);
        return response.flatMap(rp -> rp.choices().stream()
                .map(choice -> choice.message().content())
                .findFirst()
                        .map(Integer::parseInt))
                .orElseThrow();

    }

    public List<String> scoreToCriteria(ProcessedCv ci) {
        List<ScoringCriteria> scoringCriteria = List.of(
                new ScoringCriteria("Degree in Computer Science or related field", 10),
                new ScoringCriteria("Advanced degree (MSc, Ph.D.) in computer science or related field", 5),
                new ScoringCriteria("Relevant certifications (e.g., AWS Certified Developer, Microsoft Certified: Azure Developer Associate)", 5),
                new ScoringCriteria("Proficiency in Java and Python", 10),
                new ScoringCriteria("Likely to be familiar with React (or Angular, Vue.js)", 10),
                new ScoringCriteria("Understanding of databases and cloud platforms", 10),
                new ScoringCriteria("Relevant consumer tech industry experience 1 point per year", 10),
                new ScoringCriteria("Experience in a Software Engineering role ", 5),
                new ScoringCriteria("Demonstrated leadership and/or coaching role", 5),
                new ScoringCriteria("Notable projects or contributions", 10),
                new ScoringCriteria("Published work (papers, patents) in the field", 5),
                new ScoringCriteria("Contributions to open-source projects or public code repositories (e.g., GitHub)", 5),
                new ScoringCriteria("Evidence of teamwork, leadership, or other soft skills", 5),
                new ScoringCriteria("Alignment with agile methodologies", 5)
        );
        var scoringResponses = virtualThreadList(scoringCriteria, sc -> {
            GptRequest gptRequest = GptRequest.scoreToCriteria(ci, sc);
            var response = sendMessage(gptRequest);
            return new ScoringCriteriaResponse(sc.description, response.flatMap(rp -> rp.choices().stream()
                    .map(choice -> choice.message().content()).map(Integer::parseInt).findFirst()).orElse(0), sc.weight);
        });
        int totalScore = scoringResponses.stream().mapToInt(ScoringCriteriaResponse::score).sum();
        ArrayList<String> strings = new ArrayList<>();
        strings.add(STR. "\{ ci.name() } with a result of \{ totalScore }%" );
        strings.addAll(scoringResponses.stream().map(sr -> STR. "----\{ sr.description } with a score of \{ sr.score }/\{ sr.weight }" ).sorted().toList());
        return strings;
//        return List.of(ci.name(), sendMessage(scoreToCriteriaSingleRun(ci)).map(rs -> rs.choices().getFirst().message().content()).orElse("OOPS SOMETHING WENT WRONG"));
    }

    public record ScoringCriteria(String description, int weight) {
    }

    public record ScoringCriteriaResponse(String description, int score, int weight) {
    }

    private Optional<GptResponse> sendMessage(GptRequest request) {
        logger.trace("Calling GPT with request: {}", request);
        HttpRequest httpRequest = createRequest(request);
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.error("Could not retrieve the correct value for {}, code {}", "request", response.statusCode());
                return Optional.empty();
            }
            GptResponse gptResponse = gson.fromJson(response.body(), GptResponse.class);
            logger.trace("Received response from GPT: {}", gptResponse);
            GptUsage usage = gptResponse.usage();
            logger.info("Token spent: Prompt {}, Completion, {} Total, {}", usage.promptTokens(), usage.completionTokens(), usage.totalTokens());
            return Optional.of(gptResponse);
        } catch (IOException | InterruptedException e) {
            logger.error("Could not retrieve the correct value for {}, code {}", "request", e.getMessage());
            return Optional.empty();
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
