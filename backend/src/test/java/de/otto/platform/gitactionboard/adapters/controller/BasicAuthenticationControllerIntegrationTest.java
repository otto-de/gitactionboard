package de.otto.platform.gitactionboard.adapters.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.config.CodecConfig;
import lombok.SneakyThrows;
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
@AutoConfigureMockMvc
@Import(CodecConfig.class)
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {"BASIC_AUTH_USER_DETAILS_FILE_PATH=src/test/resources/.htpasswd"})
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class BasicAuthenticationControllerIntegrationTest {
  private static final String NAME = "name";
  private static final String USERNAME = "username";
  private static final String ACCESS_TOKEN = "access_token";

  private static final int ONE_DAY = 60 * 60 * 24;
  public static final String ADMIN_USERNAME = "admin";
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @SneakyThrows
  void shouldBeAbleToLoginUsingUsernameAndPassword() {
    mockMvc
        .perform(
            post("/login/basic")
                .content(
                    objectMapper.writeValueAsString(
                        new BasicAuthenticationController.AuthRequest(ADMIN_USERNAME, "password")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(cookie().value(NAME, ADMIN_USERNAME))
        .andExpect(cookie().value(USERNAME, ADMIN_USERNAME))
        .andExpect(cookie().exists(ACCESS_TOKEN))
        .andExpect(cookie().value(ACCESS_TOKEN, startsWith("Basic+")))
        .andExpect(cookie().maxAge(NAME, ONE_DAY))
        .andExpect(cookie().maxAge(USERNAME, ONE_DAY))
        .andExpect(cookie().maxAge(ACCESS_TOKEN, ONE_DAY));
  }

  @Test
  @SneakyThrows
  void shouldNoBeAbleToLoginUsingInvalidUsernameAndPassword() {
    mockMvc
        .perform(
            post("/login/basic")
                .content(
                    objectMapper.writeValueAsString(
                        new BasicAuthenticationController.AuthRequest(
                            ADMIN_USERNAME, "wrong password")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
