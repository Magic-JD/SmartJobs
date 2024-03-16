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
public class HttpConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
