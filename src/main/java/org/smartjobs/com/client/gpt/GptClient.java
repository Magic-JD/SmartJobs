package org.smartjobs.com.client.gpt;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.client.gpt.request.GptRequest;
import org.smartjobs.com.client.gpt.response.GptResponse;
import org.smartjobs.com.client.gpt.response.GptUsage;
import org.smartjobs.com.exception.categories.ApplicationExceptions.GptClientConnectionFailure;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.role.data.Role;
import org.smartjobs.com.service.role.data.ScoringCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.smartjobs.com.utils.ConcurrencyUtil.virtualThreadList;

@Component
public class GptClient {

    private static final Logger logger = LoggerFactory.getLogger(GptClient.class);
    public static final String FAILED_GPT_CALL = "Failure calling GPT, code {}";

    private final HttpClient client;
    private final Gson gson;
    private final URI clientUri;
    private final String apiKey;

    @Autowired
    public GptClient(
            HttpClient client,
            Gson gson,
            @Value("${gpt.api.site}") String site,
            @Value("${gpt.api.endpoint}") String endpoint,
            @Value("${gpt.api.key}") String apiKey) {
        this.client = client;
        this.gson = gson;
        try {
            this.clientUri = new URI(site + endpoint);
        } catch (URISyntaxException e) {
            throw new GptClientConnectionFailure(e);
        }
        this.apiKey = apiKey;
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

    public ScoringCriteriaResult scoreToCriteria(ProcessedCv ci, Role role) {

        var maxPossibleScore = role.scoringCriteria().stream().mapToDouble(ScoringCriteria::weight).sum();
        var scoringCriteria = role.scoringCriteria();
        var scoringResponses = virtualThreadList(scoringCriteria, sc -> {
            GptRequest gptRequest = GptRequest.scoreToCriteria(ci, sc);
            return sendMessage(gptRequest)
                    .flatMap(rp -> rp.choices().stream().map(choice -> choice.message().content()).findFirst())
                    .filter(rs -> {
                        if (rs.contains("SCORE")) {
                            return true;
                        }
                        logger.error("The returned response from GPT didn't contain SCORE -> {}", rs);
                        return false;
                    })
                    .map(rs -> rs.split("SCORE"))
                    .flatMap(ra -> {
                        String scoreString = ra[1].trim().replaceAll("[^0-9.]", "");
                        if (scoreString.endsWith(".")) {
                            scoreString = scoreString.substring(0, scoreString.length() - 1);
                        }
                        try {
                            double score = Double.parseDouble(scoreString);
                            return Optional.of(new ScoringCriteriaDisplay(sc.description(), ra[0].trim(), score, sc.weight()));
                        } catch (NumberFormatException e) {
                            logger.error("The returned response from GPT was not a properly formatted number. The value was {}", scoreString);
                            return Optional.empty();
                        }

                    })
                    .orElse(new ScoringCriteriaDisplay(sc.description(), "The score could not be calculated for this value", 0, 0));
        });
        double totalScore = scoringResponses.stream()
                .mapToDouble(ScoringCriteriaDisplay::score)
                .sum();
        return new ScoringCriteriaResult(UUID.randomUUID().toString(), ci.name(), (totalScore / maxPossibleScore) * 100, scoringResponses);
    }

    public record ScoringCriteriaResult(String uuid, String name, double percentage,
                                        List<ScoringCriteriaDisplay> scoringCriteria) {
    }

    public record ScoringCriteriaDisplay(String criteriaRequest, String justification, double score, int weight) {
    }

    private Optional<GptResponse> sendMessage(GptRequest request) {
        logger.trace("Calling GPT with request: {}", request);
        HttpRequest httpRequest = createRequest(request);
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.error(FAILED_GPT_CALL, response.statusCode());
                return Optional.empty();
            }
            GptResponse gptResponse = gson.fromJson(response.body(), GptResponse.class);
            logger.trace("Received response from GPT: {}", gptResponse);
            GptUsage usage = gptResponse.usage();
            logger.info("Token spent: Prompt {}, Completion, {} Total, {}", usage.promptTokens(), usage.completionTokens(), usage.totalTokens());
            return Optional.of(gptResponse);
        } catch (IOException e) {
            logger.error(FAILED_GPT_CALL, e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            logger.error(FAILED_GPT_CALL, e.getMessage());
            Thread.currentThread().interrupt();
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
