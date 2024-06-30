package org.smartjobs.core.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;

@Configuration
public class ApplicationConfig {

    @Bean
    public DecimalFormat decimalFormat() {
        return new DecimalFormat("#,###");
    }

    @Bean
    public Validator validation() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder passwordEncoder =
                (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        passwordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return passwordEncoder;
    }

    @Bean
    public SecureRandom secureRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstanceStrong();
    }
}
