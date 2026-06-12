package com.sicred.votacao.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicred.votacao.integration.dto.UserInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.flyway.enabled=true"})
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
public abstract class BaseIntegrationTest {

    private static PostgreSQLContainer<?> postgresqlContainer;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        String run = System.getenv("RUN_INTEGRATION_TESTS");
        if (!"true".equalsIgnoreCase(run)) {
            // Do not configure datasource when integration tests are disabled
            return;
        }

        postgresqlContainer = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("postgres")
                .withPassword("postgres");
        postgresqlContainer.start();

        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresqlContainer::getDriverClassName);

        // ensure container will be stopped on JVM shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (postgresqlContainer != null && postgresqlContainer.isRunning()) {
                    postgresqlContainer.stop();
                }
            } catch (Exception ignored) {
            }
        }));
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @MockBean
    protected com.sicred.votacao.integration.VoterEligibilityClient voterEligibilityClient;

    @BeforeEach
    void cleanDb() {
        // Provide default stub for external client to avoid NPEs in tests that don't mock it explicitly
        try {
            when(voterEligibilityClient.checkCpf(anyString()))
                    .thenReturn(UserInfoResponse.builder().status("ABLE_TO_VOTE").build());
        } catch (Exception ignored) {
        }

        // truncate tables to ensure isolation; adjust if necessary
        try {
            jdbcTemplate.execute("TRUNCATE voto, sessao_votacao, pauta RESTART IDENTITY CASCADE");
        } catch (Exception e) {
            // If DB not available (tests disabled), ignore
        }
    }
}
