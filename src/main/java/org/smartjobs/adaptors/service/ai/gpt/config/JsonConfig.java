package org.smartjobs.adaptors.service.ai.gpt.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.smartjobs.adaptors.service.ai.gpt.config.adaptors.GsonAdaptors;
import org.smartjobs.adaptors.service.ai.gpt.data.GptModel;
import org.smartjobs.adaptors.service.ai.gpt.data.GptRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(GptModel.class, new GsonAdaptors.GptModelTypeAdapter())
                .registerTypeAdapter(GptRole.class, new GsonAdaptors.GptRoleTypeAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

    }
}
