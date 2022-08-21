package de.otto.platform.gitactionboard;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import lombok.SneakyThrows;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class TestUtil {

  @SneakyThrows
  public static String readFile(String fileName) {
    return Files.readString(Paths.get("./src/test/resources/" + fileName), UTF_8);
  }

  @SneakyThrows
  public static void invokeGetApiAndValidate(
      MockMvc mockMvc, String endPoint, String expectedContentType, ResultMatcher resultMatcher) {
    invokeGetApiAndValidate(mockMvc, endPoint, expectedContentType, resultMatcher, null);
  }

  @SneakyThrows
  public static void invokeGetApiAndValidate(
      MockMvc mockMvc,
      String endPoint,
      String expectedContentType,
      ResultMatcher resultMatcher,
      String accessToken) {
    final MockHttpServletRequestBuilder requestBuilder =
        Objects.isNull(accessToken)
            ? get(endPoint)
            : get(endPoint).header(AUTHORIZATION, accessToken);
    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(header().stringValues(ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
        .andExpect(header().stringValues(CONTENT_TYPE, expectedContentType))
        .andExpect(resultMatcher);
  }
}
