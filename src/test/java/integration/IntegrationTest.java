package integration;

import display.CamelCaseDisplayNameGenerator;
import lombok.Getter;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.smartjobs.SmartJobs;
import org.smartjobs.core.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@Getter
@ContextConfiguration(initializers = {IntegrationTest.Initializer.class})
@SpringBootTest(classes = {SmartJobs.class})
@AutoConfigureMockMvc
@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
@Sql(
        scripts = "/init-db.sql",
        executionPhase = AFTER_TEST_METHOD
)
public abstract class IntegrationTest {

    public static final User USER = new User("username", "password", 1, List.of(() -> "USER"));

    public static final HttpHeaders HTTP_HEADERS = new HttpHeaders();

    static {
        HTTP_HEADERS.set("HX-Request", "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("postgres")
            .withUsername("integrationUser")
            .withPassword("testPass")
            .withCopyFileToContainer(MountableFile.forClasspathResource("init-db.sql"), "/docker-entrypoint-initdb.d/");

    static {
        postgres.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
