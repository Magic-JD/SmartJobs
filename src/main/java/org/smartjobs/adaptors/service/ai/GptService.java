package org.smartjobs.adaptors.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.service.ai.gpt.GptClient;
import org.smartjobs.adaptors.service.ai.gpt.request.GptRequest;
import org.smartjobs.adaptors.service.ai.gpt.response.GptResponse;
import org.smartjobs.adaptors.service.ai.gpt.response.GptUsage;
import org.smartjobs.adaptors.service.ai.gpt.response.ScoreParser;
import org.smartjobs.core.entities.Score;
import org.smartjobs.core.ports.client.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class GptService implements AiService {


    private final ScoreParser scoreParser;
    private final int userBaseScore;
    private final int cvNameChunk;
    private final GptClient client;


    @Autowired
    public GptService(GptClient client, ScoreParser scoreParser, @Value("${gpt.api.user-base-score}") int userBaseScore, @Value("${gpt.api.cv-name-chunk}") int cvNameChunk) {
        this.client = client;
        this.scoreParser = scoreParser;
        this.userBaseScore = userBaseScore;
        this.cvNameChunk = cvNameChunk;
    }

    @Override
    public Optional<String> extractCandidateName(String cv) {
        GptRequest gptRequest = GptRequest.extractCandidateName(cv.substring(0, Math.min(cv.length(), cvNameChunk)));
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
    public Optional<Score> scoreForCriteria(String cv, String criteria, long maxScore) {
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
    public Optional<Score> passForCriteria(String cv, String criteria, long maxScore) {
        GptRequest gptRequest = GptRequest.passForCriteria(cv, criteria);
        return sendMessage(gptRequest)
                .flatMap(rp -> rp.choices().stream().map(choice -> choice.message().content()).findFirst())
                .flatMap(scoreParser::parsePass)
                .map(pass -> new Score(
                        pass.justification(),
                        (pass.pass() ? 1d : 0d) * maxScore)
                );

    }

    private Optional<GptResponse> sendMessage(GptRequest request) {
        log.trace("Waiting to call GPT with request: {}", request);
        Optional<GptResponse> responseIfReceived = client.makeServiceCall(request);
        responseIfReceived.ifPresent(gptResponse -> {
            log.trace("Received response from GPT: {}", gptResponse);
            GptUsage usage = gptResponse.usage();
            log.info("Token spent: Prompt {}, Completion, {} Total, {}", usage.promptTokens(), usage.completionTokens(), usage.totalTokens());
        });
        return responseIfReceived;
    }


}
