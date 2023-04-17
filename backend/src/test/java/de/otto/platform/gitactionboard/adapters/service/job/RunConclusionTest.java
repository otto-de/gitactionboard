package de.otto.platform.gitactionboard.adapters.service.job;

import static de.otto.platform.gitactionboard.adapters.service.job.RunConclusion.ACTION_REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import de.otto.platform.gitactionboard.domain.workflow.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@Parallel
class RunConclusionTest {
  @ParameterizedTest(name = "should give SUCCESS status for {0} conclusion")
  @EnumSource(
      value = RunConclusion.class,
      names = {"SKIPPED", "SUCCESS"})
  void shouldGiveSuccessStatus(RunConclusion conclusion) {
    assertThat(RunConclusion.getStatus(conclusion)).isEqualTo(Status.SUCCESS);
  }

  @ParameterizedTest(name = "should give FAILURE status for {0} conclusion")
  @EnumSource(
      value = RunConclusion.class,
      names = {"TIMED_OUT", "CANCELLED", "FAILURE", "STARTUP_FAILURE"})
  void shouldGiveFailureStatus(RunConclusion conclusion) {
    assertThat(RunConclusion.getStatus(conclusion)).isEqualTo(Status.FAILURE);
  }

  @Test
  @DisplayName(value = "should give EXCEPTION status for ACTION_REQUIRED conclusion")
  void shouldGiveExceptionStatus() {
    assertThat(RunConclusion.getStatus(ACTION_REQUIRED)).isEqualTo(Status.EXCEPTION);
  }

  @Test
  @DisplayName(value = "should give UNKNOWN status for 'null' conclusion")
  void shouldGiveUnknownStatus() {
    assertThat(RunConclusion.getStatus(null)).isEqualTo(Status.UNKNOWN);
  }
}
