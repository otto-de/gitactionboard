package de.otto.platform.gitactionboard.adapters.service.cruisecontrol;

import static de.otto.platform.gitactionboard.fixtures.JobDetailsFixture.getJobDetailsBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Sequential
class CruiseControlServiceTest {
  private static final JobDetails JOB_DETAILS_DEPENDENCY_CHECKS = getJobDetailsBuilder().build();

  private static final JobDetails JOB_DETAILS_TALISMAN_CHECKS =
      getJobDetailsBuilder()
          .id(1132386046)
          .name("talisman-checks")
          .url("https://github.com/johndoe/hello-world/runs/1132386046")
          .lastBuildTime(Instant.parse("2020-09-18T06:11:41.000Z"))
          .triggeredEvent("pull_request")
          .build();

  private CruiseControlService cruiseControlService;

  @BeforeEach
  void setUp() {
    cruiseControlService = new CruiseControlService();
  }

  @Test
  void shouldConvertJobDetailsToCcTrayFormat() {
    final String expectedCctrayXml =
        """
            <Projects>
            <Project name="hello-world :: hello-world-checks :: dependency-checks" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:14:54Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386127" triggeredEvent="push"/>
            <Project name="hello-world :: hello-world-checks :: talisman-checks" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:11:41Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386046" triggeredEvent="pull_request"/>
            </Projects>""";

    final String ccTrayXml =
        cruiseControlService.convertToXml(
            List.of(JOB_DETAILS_DEPENDENCY_CHECKS, JOB_DETAILS_TALISMAN_CHECKS));

    assertThat(ccTrayXml).isEqualTo(expectedCctrayXml);
  }
}
