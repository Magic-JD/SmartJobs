package org.smartjobs.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DecimalFormat;

@Configuration
public class ApplicationConfig {

    @Bean
    public DecimalFormat decimalFormat() {
        return new DecimalFormat("#,###");
    }

}
