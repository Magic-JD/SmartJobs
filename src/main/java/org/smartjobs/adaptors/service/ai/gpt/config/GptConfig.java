package org.smartjobs.adaptors.service.ai.gpt.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.smartjobs.adaptors.service.ai.gpt.config.adaptors.GsonAdaptors;
import org.smartjobs.adaptors.service.ai.gpt.data.GptModel;
import org.smartjobs.adaptors.service.ai.gpt.data.GptRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class GptConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient
                .newBuilder()
                .build();
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(GptModel.class, new GsonAdaptors.GptModelTypeAdapter())
                .registerTypeAdapter(GptRole.class, new GsonAdaptors.GptRoleTypeAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

    }

    @Bean
    public Bucket bucket(@Value("${gpt.api.requests-per-minute}") int maxRequestsPerMinute) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(1)
                        .refillGreedy(maxRequestsPerMinute, Duration.ofMinutes(1))
                        .build())
                .build();
    }
}
