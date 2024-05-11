package org.smartjobs.adaptors.service.ai.gpt.response;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.service.ai.gpt.entity.Pass;
import org.smartjobs.adaptors.service.ai.gpt.entity.UnadjustedScore;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ScoreParser {

    public Optional<UnadjustedScore> parseScore(String gptResponse) {
        if (!gptResponse.contains("SCORE")) {
            log.error("The returned response from GPT didn't contain SCORE -> {}", gptResponse);
            return Optional.empty();
        }
        String[] splitResponse = gptResponse.split("SCORE");
        String scoreString = splitResponse[1].trim().replaceAll("[^0-9.]", "");
        if (scoreString.endsWith(".")) {
            scoreString = scoreString.substring(0, scoreString.length() - 1);
        }
        try {
            double score = Double.parseDouble(scoreString);
            return Optional.of(new UnadjustedScore(splitResponse[0].trim(), score));
        } catch (NumberFormatException e) {
            log.error("The returned response from GPT was not a properly formatted number. The value was {}", scoreString);
            return Optional.empty();
        }
    }

    public Optional<Pass> parsePass(String gptResponse) {
        if (!gptResponse.contains("PASS")) {
            log.error("The returned response from GPT didn't contain PASS -> {}", gptResponse);
            return Optional.empty();
        }
        String[] splitResponse = gptResponse.split("PASS");
        String scoreString = splitResponse[1].trim().toLowerCase();
        if (scoreString.endsWith(".")) {
            scoreString = scoreString.substring(0, scoreString.length() - 1);
        }
        String justification = splitResponse[0].trim();
        return Optional.ofNullable(switch (scoreString) {
            case "true" -> new Pass(justification, true);
            case "false" -> new Pass(justification, false);
            default -> {
                log.error("The returned response from GPT was not a properly formatted boolean. The value was {}", scoreString);
                yield null;
            }
        });
    }
}
