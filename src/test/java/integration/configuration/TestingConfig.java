package integration.configuration;

import org.smartjobs.core.provider.CodeProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static constants.TestConstants.CODE;

@TestConfiguration
public class TestingConfig {

    @Bean
    public CodeProvider codeProvider() {
        return () -> CODE;
    }
}
