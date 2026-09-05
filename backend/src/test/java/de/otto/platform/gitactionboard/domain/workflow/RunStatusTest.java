package de.otto.platform.gitactionboard.domain.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@Parallel
class RunStatusTest {
  @ParameterizedTest(name = "should give SLEEPING activity for COMPLETED status")
  @EnumSource(
      value = RunStatus.class,
      names = {"COMPLETED"})
  void shouldGiveSleepingActivityForCompletedStatus(RunStatus status) {
    assertThat(status.getActivity()).isEqualTo(Activity.SLEEPING);
  }

  @ParameterizedTest(name = "should give BUILDING activity for {0} status")
  @EnumSource(
      value = RunStatus.class,
      names = {"QUEUED", "IN_PROGRESS", "WAITING", "PENDING", "REQUESTED"})
  void shouldGiveBuildingActivityForNonCompletedStatus(RunStatus status) {
    assertThat(status.getActivity()).isEqualTo(Activity.BUILDING);
  }
}
