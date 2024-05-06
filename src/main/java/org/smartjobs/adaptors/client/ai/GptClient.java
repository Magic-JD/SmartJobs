package org.smartjobs.adaptors.client.ai;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.adaptors.client.ai.config.GptConfig;
import org.smartjobs.adaptors.client.ai.request.GptRequest;
import org.smartjobs.adaptors.client.ai.response.GptResponse;
import org.smartjobs.adaptors.client.ai.response.GptUsage;
import org.smartjobs.adaptors.client.ai.response.ScoreParser;
import org.smartjobs.core.client.AiClient;
import org.smartjobs.core.entities.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class GptClient implements AiClient {

    private static final Logger logger = LoggerFactory.getLogger(GptClient.class);
    public static final String FAILED_GPT_CALL = "Failure calling GPT, code {}";

    private final HttpClient client;
    private final ScoreParser scoreParser;
    private final Gson gson;
    private final URI uri;
    private final String apiKey;
    private final int userBaseScore;

    @Autowired
    public GptClient(HttpClient client, ScoreParser scoreParser, Gson gson, GptConfig config) {
        this.client = client;
        this.scoreParser = scoreParser;
        this.gson = gson;
        this.uri = config.getUri();
        this.apiKey = config.getApiKey();
        this.userBaseScore = config.getUserBaseScore();
    }

    @Override
    public Optional<String> extractCandidateName(String cv) {
        GptRequest gptRequest = GptRequest.extractCandidateName(cv.substring(0, Math.min(cv.length(), 500)));
        var response = sendMessage(gptRequest);
        return response.map(rp -> rp.choices().stream()
                .map(choice -> choice.message().content())
                .collect(Collectors.joining("\n")));
    }

    @Override
    public Optional<String> anonymizeCv(String cv) {
        GptRequest gptRequest = GptRequest.anonymousCandidateDescription(cv);
        var response = sendMessage(gptRequest);
        return response.map(rp -> rp.choices().stream()
                .map(choice -> choice.message().content())
                .collect(Collectors.joining("\n")));

    }

    @Override
    public Optional<Score> scoreForCriteria(String cv, String criteria, int maxScore) {
        GptRequest gptRequest = GptRequest.scoreForCriteria(cv, criteria, userBaseScore);
        return sendMessage(gptRequest)
                .flatMap(rp -> rp.choices().stream().map(choice -> choice.message().content()).findFirst())
                .flatMap(scoreParser::parseScore)
                .map(unadjustedScore -> new Score(
                        unadjustedScore.justification(),
                        (unadjustedScore.score() / userBaseScore) * maxScore)
                );

    }

    @Override
    public Optional<Score> passForCriteria(String cv, String criteria, int maxScore) {

        GptRequest gptRequest = GptRequest.passForCriteria(cv, criteria);
        return sendMessage(gptRequest)
                .flatMap(rp -> rp.choices().stream().map(choice -> choice.message().content()).findFirst())
                .flatMap(scoreParser::parsePass)
                .map(pass -> new Score(
                        pass.justification(),
                        (pass.pass() ? 1 : 0) * maxScore)
                );

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
                .uri(uri)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();
    }
}
