package de.otto.platform.gitactionboard.adapters.service.job;

import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.platform.gitactionboard.Parallel;
import de.otto.platform.gitactionboard.config.CodecConfig;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowRun;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Parallel
class WorkflowsRunDetailsResponseTest {
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = CodecConfig.OBJECT_MAPPER_BUILDER.build();
  }

  @Test
  @SneakyThrows
  void shouldConvertToWorkflowRun() {
    final WorkflowsRunDetailsResponse.WorkflowRunDetails workflowRunDetails =
        objectMapper
            .readValue(
                readFile("testData/workflowRuns2151835.json"), WorkflowsRunDetailsResponse.class)
            .getWorkflowRuns()
            .getFirst();

    assertThat(workflowRunDetails.toWorkflowRun())
        .isEqualTo(
            WorkflowRun.builder()
                .id(workflowRunDetails.getId())
                .status(workflowRunDetails.getStatus())
                .conclusion(workflowRunDetails.getConclusion())
                .runNumber(workflowRunDetails.getRunNumber())
                .updatedAt(workflowRunDetails.getUpdatedAt())
                .createdAt(workflowRunDetails.getCreatedAt())
                .triggeredEvent(workflowRunDetails.getTriggeredEvent())
                .runAttempt(workflowRunDetails.getRunAttempt())
                .workflowId(workflowRunDetails.getWorkflowId())
                .build());
  }
}
