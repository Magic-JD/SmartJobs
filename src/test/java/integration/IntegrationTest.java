package integration;

import display.CamelCaseDisplayNameGenerator;
import integration.configuration.TestingConfig;
import lombok.Getter;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.smartjobs.SmartJobs;
import org.smartjobs.config.TestcontainersConfiguration;
import org.smartjobs.core.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@Getter
@SpringBootTest(
        classes = {SmartJobs.class, TestingConfig.class},
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
@Sql(
        scripts = {"/schema.sql", "/data.sql"},
        executionPhase = BEFORE_TEST_METHOD
)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")

public abstract class IntegrationTest {

    public static final User USER = new User("email@email.com", "password", 1, List.of(() -> "USER"));
    public static final User ADMIN = new User("email@email.com", "password", 1, List.of(() -> "ADMIN"));

    public static final HttpHeaders HTTP_HEADERS = new HttpHeaders();

    static {
        HTTP_HEADERS.set("HX-Request", "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @NotNull
    public static ResultMatcher matchesHtml(String expected) {
        return content().string(new HtmlMatcher(expected));
    }

    private static class HtmlMatcher extends TypeSafeMatcher<String> {

        private final String expected;

        public HtmlMatcher(String html) {
            this.expected = html;
        }

        @Override
        protected boolean matchesSafely(String item) {
            String cleaned = item.replaceAll("\\s", "");
            String cleanedExpected = expected.replaceAll("\\s", "");
            return cleanedExpected.equals(cleaned);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(expected);

        }
    }


}
