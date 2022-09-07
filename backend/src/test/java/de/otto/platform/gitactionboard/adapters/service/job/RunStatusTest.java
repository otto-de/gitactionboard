package de.otto.platform.gitactionboard.adapters.service.job;

import static de.otto.platform.gitactionboard.adapters.service.job.RunStatus.COMPLETED;
import static de.otto.platform.gitactionboard.domain.workflow.Activity.BUILDING;
import static de.otto.platform.gitactionboard.domain.workflow.Activity.SLEEPING;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@Parallel
class RunStatusTest {
  @Test
  void shouldGiveSleepingActivityForCompletedStatus() {
    assertThat(RunStatus.getActivity(COMPLETED)).isEqualTo(SLEEPING);
  }

  @ParameterizedTest(name = "should give BUILDING activity for {0} status")
  @EnumSource(
      value = RunStatus.class,
      names = {"IN_PROGRESS", "QUEUED"})
  void shouldGiveBuildingActivity(RunStatus status) {
    assertThat(RunStatus.getActivity(status)).isEqualTo(BUILDING);
  }
}
