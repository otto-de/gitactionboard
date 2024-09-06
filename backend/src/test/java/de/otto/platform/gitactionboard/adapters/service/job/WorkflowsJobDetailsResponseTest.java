package de.otto.platform.gitactionboard.adapters.service.job;

import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.platform.gitactionboard.Parallel;
import de.otto.platform.gitactionboard.config.CodecConfig;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Parallel
class WorkflowsJobDetailsResponseTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = CodecConfig.OBJECT_MAPPER_BUILDER.build();
  }

  @Test
  @SneakyThrows
  void shouldConvertToWorkflowJob() {

    final WorkflowsJobDetailsResponse.WorkflowsJobDetails workflowsJobDetails =
        objectMapper
            .readValue(
                readFile("testData/jobsDetails260614021.json"), WorkflowsJobDetailsResponse.class)
            .getJobs()
            .getFirst();
    final long workflowId = 123;

    assertThat(workflowsJobDetails.toWorkflowJob(workflowId))
        .isEqualTo(
            WorkflowJob.builder()
                .id(workflowsJobDetails.getId())
                .name(workflowsJobDetails.getName())
                .status(workflowsJobDetails.getStatus())
                .conclusion(workflowsJobDetails.getConclusion())
                .startedAt(workflowsJobDetails.getStartedAt())
                .completedAt(workflowsJobDetails.getCompletedAt())
                .workflowId(workflowId)
                .workflowRunId(workflowsJobDetails.getRunId())
                .url(workflowsJobDetails.getUrl())
                .runAttempt(workflowsJobDetails.getRunAttempt())
                .build());
  }
}
