package de.otto.platform.gitactionboard.domain.service;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.repository.WorkflowRepository;
import de.otto.platform.gitactionboard.domain.service.notifications.NotificationsService;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import de.otto.platform.gitactionboard.fixtures.WorkflowsFixture;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Sequential
class PipelineServiceTest {
  private static final String CHECKS_WORKFLOW_NAME = "hello-world-checks";

  private static final JobDetails DEPENDENCY_CHECKS_JOB_DETAILS = getJobDetailsBuilder().build();

  private static final JobDetails TALISMAN_CHECKS_JOB_DETAILS =
      getJobDetailsBuilder()
          .id(1132386046)
          .name("talisman-checks")
          .url("https://github.com/johndoe/hello-world/runs/1132386046")
          .lastBuildTime(Instant.parse("2024-09-18T06:11:41.000Z"))
          .build();

  private static final JobDetails TEST_JOB_DETAILS =
      getJobDetailsBuilder()
          .id(2132286036)
          .name("test")
          .url("https://github.com/johndoe/hello-world/runs/2132286036")
          .lastBuildTime(Instant.parse("2024-09-18T06:11:48.000Z"))
          .workflowName(WorkflowsFixture.BUILD_AND_DEPLOYMENT_WORKFLOW_NAME)
          .build();

  private static final JobDetails FORMAT_JOB_DETAILS =
      getJobDetailsBuilder()
          .id(2132287038)
          .name("format")
          .url("https://github.com/johndoe/hello-world/runs/2132287038")
          .lastBuildTime(Instant.parse("2024-09-18T06:11:42.000Z"))
          .workflowName(WorkflowsFixture.BUILD_AND_DEPLOYMENT_WORKFLOW_NAME)
          .build();
  private static final String ACCESS_TOKEN = "accessToken";

  @Mock private JobDetailsService jobDetailsService;
  @Mock private WorkflowService workflowService;
  @Mock private WorkflowRepository workflowRepository;
  @Mock private NotificationsService notificationsService;

  private PipelineService pipelineService;

  @BeforeEach
  void setUp() {
    pipelineService =
        new PipelineService(
            workflowService,
            jobDetailsService,
            notificationsService,
            workflowRepository,
            List.of(REPO_NAME));
  }

  @Test
  void shouldFetchJobDetails() {
    final Workflow buildAndDeploymentWorkflow = getWorkflowBuilder().build();
    final Workflow checksWorkflow =
        getWorkflowBuilder().id(2057656).name(CHECKS_WORKFLOW_NAME).build();

    final List<Workflow> workflows = List.of(buildAndDeploymentWorkflow, checksWorkflow);
    when(workflowService.fetchWorkflows(REPO_NAME, ACCESS_TOKEN))
        .thenReturn(CompletableFuture.completedFuture(workflows));

    when(jobDetailsService.fetchJobDetails(checksWorkflow, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(
                List.of(TALISMAN_CHECKS_JOB_DETAILS, DEPENDENCY_CHECKS_JOB_DETAILS)));

    when(jobDetailsService.fetchJobDetails(buildAndDeploymentWorkflow, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(List.of(TEST_JOB_DETAILS, FORMAT_JOB_DETAILS)));
    when(workflowRepository.save(workflows)).thenReturn(CompletableFuture.runAsync(() -> {}));

    final List<JobDetails> jobDetails = pipelineService.fetchJobs(ACCESS_TOKEN);

    assertThat(jobDetails)
        .hasSize(4)
        .containsExactlyInAnyOrder(
            TALISMAN_CHECKS_JOB_DETAILS,
            DEPENDENCY_CHECKS_JOB_DETAILS,
            TEST_JOB_DETAILS,
            FORMAT_JOB_DETAILS);

    verify(notificationsService).sendNotificationsForWorkflowJobs(jobDetails);
    verify(workflowService).fetchWorkflows(REPO_NAME, ACCESS_TOKEN);
    verify(jobDetailsService).fetchJobDetails(buildAndDeploymentWorkflow, ACCESS_TOKEN);
    verify(jobDetailsService).fetchJobDetails(checksWorkflow, ACCESS_TOKEN);
  }

  @Test
  void shouldIgnoreListOfEmptyJobs() {
    final Workflow workflow1 = getWorkflowBuilder().build();
    final Workflow workflow2 = getWorkflowBuilder().id(2151836).name("checks").build();

    final List<Workflow> workflows = List.of(workflow1, workflow2);

    when(workflowRepository.save(workflows)).thenReturn(CompletableFuture.runAsync(() -> {}));
    when(workflowService.fetchWorkflows(REPO_NAME, ACCESS_TOKEN))
        .thenReturn(CompletableFuture.completedFuture(workflows));

    when(jobDetailsService.fetchJobDetails(workflow1, ACCESS_TOKEN))
        .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));
    when(jobDetailsService.fetchJobDetails(workflow2, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(
                List.of(TALISMAN_CHECKS_JOB_DETAILS, DEPENDENCY_CHECKS_JOB_DETAILS)));

    final List<JobDetails> jobDetails = pipelineService.fetchJobs(ACCESS_TOKEN);

    assertThat(jobDetails)
        .containsExactlyInAnyOrder(TALISMAN_CHECKS_JOB_DETAILS, DEPENDENCY_CHECKS_JOB_DETAILS);
    verify(notificationsService).sendNotificationsForWorkflowJobs(jobDetails);
  }
}
