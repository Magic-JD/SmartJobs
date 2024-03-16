package org.smartjobs.com.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.smartjobs.com.client.gpt.data.GptModel;
import org.smartjobs.com.client.gpt.data.GptRole;
import org.smartjobs.com.config.adaptors.GsonAdaptors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

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
