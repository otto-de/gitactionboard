package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.fixtures.JobDetailsFixture.TRIGGERED_EVENT;
import static de.otto.platform.gitactionboard.fixtures.JobDetailsFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpStatus.OK;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.adapters.service.cruisecontrol.CruiseControlService;
import de.otto.platform.gitactionboard.adapters.service.cruisecontrol.Project;
import de.otto.platform.gitactionboard.domain.service.PipelineService;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@Sequential
class GithubControllerTest {

  @Mock private PipelineService pipelineService;

  @Mock private CruiseControlService cruiseControlService;

  private GithubController githubController;

  @BeforeEach
  void setUp() {
    githubController = new GithubController(pipelineService, cruiseControlService);
  }

  @ParameterizedTest
  @NullSource
  @CsvSource(value = {"accessToken"})
  void shouldFetchJobDataAsXml(String accessToken) {
    final String cctrayXml =
        """
            <Projects>
            <Project name="hello-world :: hello-world-checks :: dependency-checks" activity="Sleeping" lastBuildStatus="Success" lastBuildLabel="206" lastBuildTime="2020-09-18T06:14:54Z" webUrl="https://github.com/johndoe/hello-world/runs/1132386127" triggeredEvent="push"/>
            </Projects>""";

    final List<JobDetails> jobs = List.of(getJobDetailsBuilder().build());

    when(pipelineService.fetchJobs(accessToken)).thenReturn(jobs);
    when(cruiseControlService.convertToXml(jobs)).thenReturn(cctrayXml);

    final ResponseEntity<String> cctrayResponse = githubController.getCctrayXml(accessToken);

    assertThat(cctrayResponse.getStatusCode()).isEqualTo(OK);
    assertThat(cctrayResponse.getHeaders())
        .containsEntry(ACCESS_CONTROL_ALLOW_ORIGIN, List.of("*"));
    assertThat(cctrayResponse.getBody()).isEqualTo(cctrayXml);
  }

  @ParameterizedTest
  @NullSource
  @CsvSource(value = {"accessToken"})
  void shouldFetchJobDataAsJson(String accessToken) {
    final List<JobDetails> jobs = List.of(getJobDetailsBuilder().build());
    final List<Project> projects =
        List.of(
            Project.builder()
                .name(String.format("%s :: dependency-checks :: hello-world-checks", REPO_NAME))
                .lastBuildTime(Instant.parse("2020-09-18T06:14:54.000Z"))
                .activity("Sleeping")
                .lastBuildLabel("206")
                .lastBuildStatus("Success")
                .webUrl("https://github.com/johndoe/hello-world/runs/1132386127")
                .triggeredEvent(TRIGGERED_EVENT)
                .build());

    when(pipelineService.fetchJobs(accessToken)).thenReturn(jobs);
    when(cruiseControlService.convertToJson(jobs)).thenReturn(projects);

    final ResponseEntity<List<Project>> cctrayResponse = githubController.getCctray(accessToken);

    assertThat(cctrayResponse.getStatusCode()).isEqualTo(OK);
    assertThat(cctrayResponse.getHeaders())
        .containsEntry(ACCESS_CONTROL_ALLOW_ORIGIN, List.of("*"));
    assertThat(cctrayResponse.getBody()).isEqualTo(projects);
  }
}
