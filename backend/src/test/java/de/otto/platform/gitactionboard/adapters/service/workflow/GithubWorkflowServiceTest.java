package de.otto.platform.gitactionboard.adapters.service.workflow;

import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.TestUtil;
import de.otto.platform.gitactionboard.adapters.service.GithubApiService;
import de.otto.platform.gitactionboard.adapters.service.workflow.WorkflowsResponse.WorkflowIdentifier;
import de.otto.platform.gitactionboard.config.CodecConfig;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
@Sequential
class GithubWorkflowServiceTest {

  private static final String ACCESS_TOKEN = "accessToken";
  private final ObjectMapper objectMapper = CodecConfig.OBJECT_MAPPER_BUILDER.build();
  @Mock private GithubApiService apiService;

  private GithubWorkflowService githubWorkflowService;

  @BeforeEach
  void setUp() {
    githubWorkflowService = new GithubWorkflowService(apiService);
  }

  @Test
  @SneakyThrows
  void shouldFetchWorkflowsForGivenRepository() {
    final WorkflowsResponse workflowsResponse =
        objectMapper.readValue(
            TestUtil.readFile("testData/workflows.json"), WorkflowsResponse.class);

    when(apiService.getForObject(
            "/%s/actions/workflows".formatted(REPO_NAME), ACCESS_TOKEN, WorkflowsResponse.class))
        .thenReturn(workflowsResponse);

    final List<Workflow> workflows =
        githubWorkflowService.fetchWorkflows(REPO_NAME, ACCESS_TOKEN).get();

    final List<WorkflowIdentifier> workflowsFromResponse = workflowsResponse.getWorkflows();

    assertThat(workflows).hasSize(2);

    assertThat(workflows.getFirst())
        .isEqualTo(
            Workflow.builder()
                .id(workflowsFromResponse.getFirst().getId())
                .name(workflowsFromResponse.getFirst().getName())
                .repoName(REPO_NAME)
                .build());

    assertThat(workflows.get(1))
        .isEqualTo(
            Workflow.builder()
                .id(workflowsFromResponse.get(1).getId())
                .name(workflowsFromResponse.get(1).getName())
                .repoName(REPO_NAME)
                .build());
  }

  @Test
  @SneakyThrows
  void shouldReturnEmptyListWhenResponseIsNull() {
    final List<Workflow> workflows =
        githubWorkflowService.fetchWorkflows(REPO_NAME, ACCESS_TOKEN).get();

    assertThat(workflows).isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldReturnEmptyListIfAnyExceptionOccurs() {
    when(apiService.getForObject(
            "/%s/actions/workflows".formatted(REPO_NAME), ACCESS_TOKEN, WorkflowsResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    final List<Workflow> workflows =
        githubWorkflowService.fetchWorkflows(REPO_NAME, ACCESS_TOKEN).get();

    assertThat(workflows).isEmpty();
  }
}
