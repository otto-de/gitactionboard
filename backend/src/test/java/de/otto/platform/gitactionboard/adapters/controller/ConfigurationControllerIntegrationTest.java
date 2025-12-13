package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.TestUtil.mockGetRequestWithPageQueryParam;
import static de.otto.platform.gitactionboard.TestUtil.mockRequest;
import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static de.otto.platform.gitactionboard.TestUtil.verifyRequest;
import static de.otto.platform.gitactionboard.TestUtil.verifyRequestWithPageQueryParams;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.verify.VerificationTimes.exactly;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.Sequential;
import java.util.Base64;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Parameter;
import org.mockserver.springtest.MockServerTest;
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
      "ENABLE_GITHUB_SECRETS_SCAN_ALERTS_MONITORING=",
      "REPO_NAMES=hello-world,dummy-repo",
    })
@Sequential
@MockServerTest
class ConfigurationControllerIntegrationTest {
  private static final String AUTHORIZATION_HEADER_VALUE =
      "Basic %s".formatted(Base64.getEncoder().encodeToString("admin:password".getBytes(UTF_8)));

  private static final String GITHUB_API_BASE_PATH = "/repos/johndoe";
  private static final String DUMMY_REPO_BRANCHES_URL =
      "%s/dummy-repo/branches".formatted(GITHUB_API_BASE_PATH);
  private static final String HELLO_WORLD_BRANCHES_URL =
      "%s/hello-world/branches".formatted(GITHUB_API_BASE_PATH);
  private static final String PER_PAGE = "per_page";
  private static final String ONE_HUNDRED = "100";

  @Autowired private MockMvc mockMvc;

  private MockServerClient mockServerClient;

  @Test
  @SneakyThrows
  @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
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
  @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
  void shouldFetchListOfRepositories() {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/config/repository-names")
                .header(AUTHORIZATION, AUTHORIZATION_HEADER_VALUE))
        .andExpect(status().isOk())
        .andExpect(header().stringValues(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]").value("hello-world"))
        .andExpect(jsonPath("$[1]").value("dummy-repo"));
  }

  @Test
  @SneakyThrows
  void shouldProvideAvailableUniqueBranchNames() {
    mockGetRequestWithPageQueryParam(
        mockServerClient,
        HELLO_WORLD_BRANCHES_URL,
        readFile("testData/helloWorldBranches.json"),
        PER_PAGE);

    mockGetRequestWithPageQueryParam(
        mockServerClient,
        DUMMY_REPO_BRANCHES_URL,
        readFile("testData/dummyRepoBranches.json"),
        PER_PAGE);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/config/branch-names")
                .header(AUTHORIZATION, AUTHORIZATION_HEADER_VALUE))
        .andExpect(status().isOk())
        .andExpect(header().stringValues(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]").value("develop"))
        .andExpect(jsonPath("$[1]").value("feature/new-login-flow"))
        .andExpect(jsonPath("$[2]").value("main"))
        .andExpect(jsonPath("$[3]").value("release/v2.1.0"));

    verifyRequestWithPageQueryParams(
        mockServerClient, HELLO_WORLD_BRANCHES_URL, "", Parameter.param(PER_PAGE, ONE_HUNDRED));
    verifyRequestWithPageQueryParams(
        mockServerClient, DUMMY_REPO_BRANCHES_URL, "", Parameter.param(PER_PAGE, ONE_HUNDRED));

    verifyRequest(mockServerClient, request().withPath(HELLO_WORLD_BRANCHES_URL), exactly(1));
    verifyRequest(mockServerClient, request().withPath(DUMMY_REPO_BRANCHES_URL), exactly(1));
  }

  @Test
  @SneakyThrows
  void shouldIgnoreErrorForSomeReposAndProvideAvailableUniqueBranchNames() {

    mockGetRequestWithPageQueryParam(
        mockServerClient,
        HELLO_WORLD_BRANCHES_URL,
        readFile("testData/helloWorldBranches.json"),
        PER_PAGE);

    mockRequest(
        mockServerClient,
        request().withMethod("GET").withPath(DUMMY_REPO_BRANCHES_URL),
        SC_NOT_FOUND,
        "Not found",
        APPLICATION_JSON_VALUE);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/config/branch-names")
                .header(AUTHORIZATION, AUTHORIZATION_HEADER_VALUE))
        .andExpect(status().isOk())
        .andExpect(header().stringValues(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]").value("develop"))
        .andExpect(jsonPath("$[1]").value("feature/new-login-flow"))
        .andExpect(jsonPath("$[2]").value("main"));

    verifyRequestWithPageQueryParams(
        mockServerClient, HELLO_WORLD_BRANCHES_URL, "", Parameter.param(PER_PAGE, ONE_HUNDRED));
    verifyRequestWithPageQueryParams(
        mockServerClient, DUMMY_REPO_BRANCHES_URL, "", Parameter.param(PER_PAGE, ONE_HUNDRED));

    verifyRequest(mockServerClient, request().withPath(HELLO_WORLD_BRANCHES_URL), exactly(1));
    verifyRequest(mockServerClient, request().withPath(DUMMY_REPO_BRANCHES_URL), exactly(1));
  }

  @Test
  @SneakyThrows
  void shouldReturnEmptyListIfUnableToFetchBranchNames() {
    mockRequest(
        mockServerClient,
        request().withMethod("GET"),
        SC_NOT_FOUND,
        "Not found",
        APPLICATION_JSON_VALUE);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/config/branch-names")
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
        .andExpect(jsonPath("$", empty()));

    verifyRequestWithPageQueryParams(
        mockServerClient, HELLO_WORLD_BRANCHES_URL, "", Parameter.param(PER_PAGE, ONE_HUNDRED));
    verifyRequestWithPageQueryParams(
        mockServerClient, DUMMY_REPO_BRANCHES_URL, "", Parameter.param(PER_PAGE, ONE_HUNDRED));

    verifyRequest(mockServerClient, request().withPath(HELLO_WORLD_BRANCHES_URL), exactly(1));
    verifyRequest(mockServerClient, request().withPath(DUMMY_REPO_BRANCHES_URL), exactly(1));
  }
}
