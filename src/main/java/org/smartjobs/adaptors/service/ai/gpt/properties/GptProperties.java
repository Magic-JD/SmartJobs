package org.smartjobs.adaptors.service.ai.gpt.properties;

import lombok.Getter;
import org.smartjobs.core.exception.categories.ApplicationExceptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@Getter
public class GptProperties {

    private final URI uri;
    private final String apiKey;
    private final int maxRetries;
    private final int initialTimeoutSeconds;

    public GptProperties(@Value("${gpt.api.url}") String url,
                         @Value("${gpt.api.key}") String apiKey,
                         @Value("${gpt.api.max-retries") int maxRetries,
                         @Value("${gpt.api.initial-timeout-seconds") int initialTimeoutSeconds
    ) {
        try {
            this.uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new ApplicationExceptions.GptClientConnectionFailure(e, url);
        }
        this.apiKey = apiKey;
        this.maxRetries = maxRetries;
        this.initialTimeoutSeconds = initialTimeoutSeconds;
    }
}