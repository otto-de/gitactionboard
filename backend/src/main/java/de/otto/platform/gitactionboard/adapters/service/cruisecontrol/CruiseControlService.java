package de.otto.platform.gitactionboard.adapters.service.cruisecontrol;

import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class CruiseControlService {
  public String convertToXml(List<JobDetails> jobs) {
    final Stream<String> projectStream = getProjectStream(jobs).map(this::createCctrayProject);

    return Stream.concat(
            Stream.concat(Stream.of("<Projects>"), projectStream), Stream.of("</Projects>"))
        .collect(Collectors.joining("\n"));
  }

  public List<Project> convertToJson(List<JobDetails> jobs) {
    return getProjectStream(jobs).toList();
  }

  private Stream<Project> getProjectStream(List<JobDetails> jobs) {
    return jobs.stream().map(Project::fromJobDetails);
  }

  private String createCctrayProject(Project project) {
    return String.format(
        "<Project name=\"%s\" activity=\"%s\" lastBuildStatus=\"%s\" lastBuildLabel=\"%s\" lastBuildTime=\"%s\" webUrl=\"%s\" triggeredEvent=\"%s\"/>",
        project.getName(),
        project.getActivity(),
        project.getLastBuildStatus(),
        project.getLastBuildLabel(),
        project.getLastBuildTime(),
        project.getWebUrl(),
        project.getTriggeredEvent());
  }
}
