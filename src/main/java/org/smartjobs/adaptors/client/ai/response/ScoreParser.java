package org.smartjobs.adaptors.client.ai.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.adaptors.client.ai.entity.UnadjustedScore;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScoreParser {

    private static final Logger logger = LoggerFactory.getLogger(ScoreParser.class);

    public Optional<UnadjustedScore> parseScore(String gptResponse) {
        if (!gptResponse.contains("SCORE")) {
            logger.error("The returned response from GPT didn't contain SCORE -> {}", gptResponse);
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
            logger.error("The returned response from GPT was not a properly formatted number. The value was {}", scoreString);
            return Optional.empty();
        }
    }
}
