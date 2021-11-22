package de.otto.platform.gitactionboard.adapters.service.workflow;

import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.platform.gitactionboard.TestUtil;
import de.otto.platform.gitactionboard.adapters.service.workflow.WorkflowsResponse.WorkflowIdentifier;
import de.otto.platform.gitactionboard.config.CodecConfig;
import de.otto.platform.gitactionboard.domain.Workflow;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GithubWorkflowServiceTest {

  private final ObjectMapper objectMapper = CodecConfig.OBJECT_MAPPER_BUILDER.build();
  @Mock private RestTemplate restTemplate;

  private GithubWorkflowService githubWorkflowService;

  @BeforeEach
  void setUp() {
    githubWorkflowService = new GithubWorkflowService(restTemplate);
  }

  @Test
  @SneakyThrows
  void shouldFetchWorkflowsForGivenRepository() {
    final WorkflowsResponse workflowsResponse =
        objectMapper.readValue(
            TestUtil.readFile("testData/workflows.json"), WorkflowsResponse.class);

    when(restTemplate.getForObject(
            String.format("/%s/actions/workflows", REPO_NAME), WorkflowsResponse.class))
        .thenReturn(workflowsResponse);

    final List<Workflow> workflows = githubWorkflowService.fetchWorkflows(REPO_NAME).get();

    final List<WorkflowIdentifier> workflowsFromResponse = workflowsResponse.getWorkflows();

    assertThat(workflows).hasSameSizeAs(workflowsFromResponse);

    assertThat(workflows.get(0))
        .isEqualTo(
            Workflow.builder()
                .id(workflowsFromResponse.get(0).getId())
                .name(workflowsFromResponse.get(0).getName())
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
    final List<Workflow> workflows = githubWorkflowService.fetchWorkflows(REPO_NAME).get();

    assertThat(workflows).isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldReturnEmptyListIfAnyExceptionOccurs() {
    when(restTemplate.getForObject(
            String.format("/%s/actions/workflows", REPO_NAME), WorkflowsResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    final List<Workflow> workflows = githubWorkflowService.fetchWorkflows(REPO_NAME).get();

    assertThat(workflows).isEmpty();
  }
}
