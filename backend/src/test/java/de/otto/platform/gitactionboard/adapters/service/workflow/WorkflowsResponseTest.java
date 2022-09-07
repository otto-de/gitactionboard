package de.otto.platform.gitactionboard.adapters.service.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import de.otto.platform.gitactionboard.adapters.service.workflow.WorkflowsResponse.WorkflowIdentifier;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Parallel
class WorkflowsResponseTest {

  @Nested
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
  class WorkflowIdentifierTest {
    @ParameterizedTest
    @CsvSource(
        value = {
          ".github/workflows/test.yml,active,true",
          ".github/workflows/test.yml,disable,false",
          "workflows/test.yml,active,false",
          "test.yml,disable,false",
          "'',disable,false",
        })
    void shouldValidateWorkflowState(String path, String state, Boolean expectedStatus) {
      final WorkflowIdentifier workflowIdentifier =
          WorkflowIdentifier.builder().id(1).name("test").path(path).state(state).build();

      assertThat(workflowIdentifier.isActive()).isEqualTo(expectedStatus);
    }
  }
}
