package org.smartjobs.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**.html").denyAll()
                .requestMatchers("/", "/login", "/styles/style.css").permitAll()
                .anyRequest().authenticated());
        http.formLogin(_ -> {
        });
        http.logout(_ -> {
        });
//        http.authorizeHttpRequests(auth -> auth.)
//                .anyRequest()
//                .permitAll());
        return http.build();
    }


}
