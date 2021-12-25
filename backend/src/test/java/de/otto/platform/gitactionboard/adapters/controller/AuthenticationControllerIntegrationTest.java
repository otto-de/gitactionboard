package de.otto.platform.gitactionboard.adapters.controller;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.otto.platform.gitactionboard.IntegrationTest;
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
      "BASIC_AUTH_USER_DETAILS_FILE_PATH=src/test/resources/.htpasswd"
    })
class AuthenticationControllerIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  @SneakyThrows
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void shouldGiveListOfAvailableAuths() {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/available-auths"))
        .andExpect(status().isOk())
        .andExpect(header().stringValues(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().json("[\"BASIC_AUTH\"]"));
  }
}
