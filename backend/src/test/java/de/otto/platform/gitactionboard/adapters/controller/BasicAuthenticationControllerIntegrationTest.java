package de.otto.platform.gitactionboard.adapters.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.Parallel;
import de.otto.platform.gitactionboard.config.CodecConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DirtiesContext
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
@Parallel
class BasicAuthenticationControllerIntegrationTest {
  private static final String NAME = "name";
  private static final String USERNAME = "username";
  private static final String ACCESS_TOKEN = "access_token";

  private static final int ONE_DAY = 60 * 60 * 24;
  private static final String ADMIN_USERNAME = "admin";

  @SneakyThrows
  private static void validateSuccessApiCall(
      String contextPath, MockMvc mockMvc, ObjectMapper objectMapper) {
    mockMvc
        .perform(
            post("/login/basic")
                .content(getPostRequestBody(objectMapper, "password"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(cookie().value(NAME, ADMIN_USERNAME))
        .andExpect(cookie().value(USERNAME, ADMIN_USERNAME))
        .andExpect(cookie().exists(ACCESS_TOKEN))
        .andExpect(cookie().value(ACCESS_TOKEN, startsWith("Basic+")))
        .andExpect(cookie().maxAge(NAME, ONE_DAY))
        .andExpect(cookie().maxAge(USERNAME, ONE_DAY))
        .andExpect(cookie().maxAge(ACCESS_TOKEN, ONE_DAY))
        .andExpect(cookie().path(NAME, contextPath))
        .andExpect(cookie().path(USERNAME, contextPath))
        .andExpect(cookie().path(ACCESS_TOKEN, contextPath));
  }

  @SneakyThrows
  private static String getPostRequestBody(ObjectMapper objectMapper, String password) {
    return objectMapper.writeValueAsString(
        new BasicAuthenticationController.AuthRequest(ADMIN_USERNAME, password));
  }

  @SneakyThrows
  private static void validateUnauthorisedApiCall(MockMvc mockMvc, ObjectMapper objectMapper) {
    mockMvc
        .perform(
            post("/login/basic")
                .content(getPostRequestBody(objectMapper, "wrong password"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Nested
  @AutoConfigureMockMvc
  @Import(CodecConfig.class)
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {"BASIC_AUTH_USER_DETAILS_FILE_PATH=src/test/resources/.htpasswd"})
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
  class WithDefaultContextPathTest {
    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void shouldBeAbleToLoginUsingUsernameAndPassword() {
      validateSuccessApiCall("/", mockMvc, objectMapper);
    }

    @Test
    @SneakyThrows
    void shouldNoBeAbleToLoginUsingInvalidUsernameAndPassword() {
      validateUnauthorisedApiCall(mockMvc, objectMapper);
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @Import(CodecConfig.class)
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {
        "BASIC_AUTH_USER_DETAILS_FILE_PATH=src/test/resources/.htpasswd",
        "server.servlet.context-path=/test"
      })
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
  class WithCustomContextPathTest {
    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void shouldBeAbleToLoginUsingUsernameAndPassword() {
      validateSuccessApiCall("/test", mockMvc, objectMapper);
    }

    @Test
    @SneakyThrows
    void shouldNoBeAbleToLoginUsingInvalidUsernameAndPassword() {
      validateUnauthorisedApiCall(mockMvc, objectMapper);
    }
  }
}
