package de.otto.platform.gitactionboard.adapters.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.Parallel;
import java.util.Base64;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@IntegrationTest
@DirtiesContext
@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
      "GITHUB_OAUTH2_CLIENT_ID=",
      "BASIC_AUTH_USER_DETAILS_FILE_PATH=src/test/resources/.htpasswd",
      "ENABLE_GITHUB_SECRETS_SCAN_ALERTS_MONITORING="
    })
@Parallel
class ConfigurationControllerIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  @SneakyThrows
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void shouldGiveAvailableConfig() {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/config"))
        .andExpect(status().isOk())
        .andExpect(header().stringValues(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.availableAuths").isArray())
        .andExpect(jsonPath("$.availableAuths", hasSize(1)))
        .andExpect(jsonPath("$.availableAuths[0]").value("BASIC_AUTH"))
        .andExpect(jsonPath("$.githubSecretsScanMonitoringEnabled").value("false"))
        .andExpect(jsonPath("$.version").exists());
  }

  @Test
  @SneakyThrows
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void shouldFetchListOfRepositories() {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/config/repository-names")
                .header(
                    AUTHORIZATION,
                    "Basic %s"
                        .formatted(
                            new String(
                                Base64.getEncoder()
                                    .encode("%s:%s".formatted("admin", "password").getBytes(UTF_8)),
                                UTF_8))))
        .andExpect(status().isOk())
        .andExpect(header().stringValues(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]").value("hello-world"));
  }
}
