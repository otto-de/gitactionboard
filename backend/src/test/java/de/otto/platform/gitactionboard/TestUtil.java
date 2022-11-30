package de.otto.platform.gitactionboard;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpStatus.SC_OK;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.once;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import lombok.SneakyThrows;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.Parameter;
import org.mockserver.verify.VerificationTimes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class TestUtil {

  @SneakyThrows
  public static String readFile(String fileName) {
    return Files.readString(Paths.get("./src/test/resources/%s".formatted(fileName)), UTF_8);
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

  public static void verifyRequest(
      MockServerClient mockServerClient, String url, String authToken) {
    verifyRequest(mockServerClient, url, authToken, once());
  }

  public static void verifyRequest(
      MockServerClient mockServerClient,
      String url,
      String authToken,
      VerificationTimes verificationTimes) {
    verifyRequest(
        mockServerClient,
        request().withPath(url).withHeader(AUTHORIZATION, authToken),
        verificationTimes);
  }

  public static void verifyRequest(
      MockServerClient mockServerClient,
      HttpRequest requestDefinition,
      VerificationTimes verificationTimes) {
    mockServerClient.verify(requestDefinition, verificationTimes);
  }

  public static void verifyRequestWithPageQueryParams(
      MockServerClient mockServerClient, String url, String authToken, Parameter... queryParams) {
    verifyRequest(
        mockServerClient,
        request()
            .withPath(url)
            .withQueryStringParameters(queryParams)
            .withHeader(AUTHORIZATION, authToken),
        once());
  }

  public static void mockRequest(
      MockServerClient mockServerClient, HttpRequest requestDefinition, String responseBody) {
    mockRequest(mockServerClient, requestDefinition, SC_OK, responseBody, APPLICATION_JSON_VALUE);
  }

  public static void mockRequest(
      MockServerClient mockServerClient,
      HttpRequest requestDefinition,
      int statusCode,
      String responseBody,
      String contentType) {
    mockServerClient
        .when(requestDefinition)
        .respond(
            response()
                .withStatusCode(statusCode)
                .withHeader(CONTENT_TYPE, contentType)
                .withBody(responseBody));
  }

  public static void mockGetRequest(
      MockServerClient mockServerClient, String url, String responseBody) {
    mockRequest(mockServerClient, request().withMethod("GET").withPath(url), responseBody);
  }

  public static void mockGetRequestWithPageQueryParam(
      MockServerClient mockServerClient, String url, String responseBody, String... queryParams) {

    mockRequest(
        mockServerClient,
        request()
            .withMethod("GET")
            .withPath(url)
            .withQueryStringParameters(Arrays.stream(queryParams).map(Parameter::param).toList()),
        responseBody);
  }
}
