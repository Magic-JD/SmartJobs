package org.smartjobs.core.config;

import org.smartjobs.core.service.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String LOGIN_URL = "/login";

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/",
                        "/login",
                        "/login/register",
                        "/login/registration",
                        "/login/verify/*",
                        "/error",
                        "/styles/style.css",
                        "/favicon/favicon.ico",
                        "/swagger-ui/**",
                        "/v3/api-docs/**", // Added for OpenAPI v3
                        "/v2/api-docs",   // Kept for compatibility if needed
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated());
        http.formLogin(formLogin -> formLogin
                .loginPage(LOGIN_URL).defaultSuccessUrl("/", true));
        http.logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }


    @Bean
    public AuthenticationProvider authProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

}
