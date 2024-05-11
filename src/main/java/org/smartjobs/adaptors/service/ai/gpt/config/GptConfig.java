package org.smartjobs.adaptors.service.ai.gpt.config;

import lombok.Getter;
import org.smartjobs.core.exception.categories.ApplicationExceptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@Getter
public class GptConfig {

    private final URI uri;
    private final String apiKey;
    private final int userBaseScore;
    private final int maxRequestsPerMinute;

    public GptConfig(@Value("${gpt.api.user-base-score}") int userBaseScore, @Value("${gpt.api.url}") String url,
                     @Value("${gpt.api.key}") String apiKey, @Value("${gpt.api.requests-per-minute}") int maxRequestsPerMinute) {
        try {
            this.uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new ApplicationExceptions.GptClientConnectionFailure(e, url);
        }
        this.apiKey = apiKey;
        this.userBaseScore = userBaseScore;
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }
}