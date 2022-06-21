package de.otto.platform.gitactionboard.adapters.service.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.JobDetails;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TeamsNotificationMessagePayload {
  @JsonProperty("@type")
  String type;

  @JsonProperty("@context")
  String context;

  String themeColor;
  String summary;
  List<Section> sections;
  List<PotentialAction> potentialAction;

  @Value
  @Builder
  public static class Section {
    String activityTitle;
    Boolean markdown;
    List<Fact> facts;
  }

  @Value
  @Builder
  public static class PotentialAction {
    @JsonProperty("@type")
    String type;

    String name;
    List<Target> targets;
  }

  @Value
  @Builder
  public static class Fact {
    String name;
    String value;
  }

  @Value
  @Builder
  public static class Target {
    String os;
    String uri;
  }

  public static TeamsNotificationMessagePayload from(JobDetails jobDetails) {
    final String summary = getSummary(jobDetails);
    return TeamsNotificationMessagePayload.builder()
        .type("MessageCard")
        .context("https://schema.org/extensions")
        .themeColor("c5413a")
        .summary(summary)
        .sections(
            List.of(
                Section.builder()
                    .activityTitle(summary)
                    .markdown(true)
                    .facts(
                        List.of(
                            Fact.builder()
                                .name("Repository:")
                                .value(jobDetails.getRepoName())
                                .build(),
                            Fact.builder()
                                .name("Workflow Name:")
                                .value(jobDetails.getWorkflowName())
                                .build(),
                            Fact.builder().name("Job Name:").value(jobDetails.getName()).build()))
                    .build()))
        .potentialAction(
            List.of(
                PotentialAction.builder()
                    .type("OpenUri")
                    .name("View in GitHub")
                    .targets(
                        List.of(Target.builder().os("default").uri(jobDetails.getUrl()).build()))
                    .build()))
        .build();
  }

  private static String getSummary(JobDetails jobDetails) {
    return String.format(
        "[%s] Run failed: %s::%s",
        jobDetails.getRepoName(), jobDetails.getWorkflowName(), jobDetails.getName());
  }
}
