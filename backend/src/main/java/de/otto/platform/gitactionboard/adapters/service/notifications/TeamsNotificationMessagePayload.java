package de.otto.platform.gitactionboard.adapters.service.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class TeamsNotificationMessagePayload {
  private static final String RED_COLOR = "c5413a";

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

  public static TeamsNotificationMessagePayload from(SecretsScanDetails secretsScanDetails) {
    final String summary = getSummary(secretsScanDetails);
    return buildMessagePayload(
        summary, secretsScanDetails.getUrl(), buildFacts(secretsScanDetails));
  }

  public static TeamsNotificationMessagePayload from(JobDetails jobDetails) {
    final String summary = getSummary(jobDetails);
    return buildMessagePayload(summary, jobDetails.getUrl(), buildFacts(jobDetails));
  }

  public static TeamsNotificationMessagePayload from(
      CodeStandardViolationDetails codeStandardViolationDetails) {
    final String summary = getSummary(codeStandardViolationDetails);
    return buildMessagePayload(
        summary, codeStandardViolationDetails.getUrl(), buildFacts(codeStandardViolationDetails));
  }

  private static List<Fact> buildFacts(SecretsScanDetails secretsScanDetails) {
    return List.of(
        buildFact("Repository:", secretsScanDetails.getRepoName()),
        buildFact("Secret Type:", secretsScanDetails.getName()));
  }

  private static List<Fact> buildFacts(CodeStandardViolationDetails codeStandardViolationDetails) {
    return List.of(
        buildFact("Repository:", codeStandardViolationDetails.getRepoName()),
        buildFact("Violation Type:", codeStandardViolationDetails.getName()));
  }

  private static List<Fact> buildFacts(JobDetails jobDetails) {
    return List.of(
        buildFact("Repository:", jobDetails.getRepoName()),
        buildFact("Workflow Name:", jobDetails.getWorkflowName()),
        buildFact("Job Name:", jobDetails.getName()));
  }

  private static Fact buildFact(String name, String value) {
    return Fact.builder().name(name).value(value).build();
  }

  private static TeamsNotificationMessagePayload buildMessagePayload(
      String summary, @NonNull String url, List<Fact> facts) {
    return TeamsNotificationMessagePayload.builder()
        .type("MessageCard")
        .context("https://schema.org/extensions")
        .themeColor(RED_COLOR)
        .summary(summary)
        .sections(
            List.of(Section.builder().activityTitle(summary).markdown(true).facts(facts).build()))
        .potentialAction(
            List.of(
                PotentialAction.builder()
                    .type("OpenUri")
                    .name("View in GitHub")
                    .targets(List.of(Target.builder().os("default").uri(url).build()))
                    .build()))
        .build();
  }

  private static String getSummary(SecretsScanDetails secretsScanDetails) {
    return "[%s] Exposed secret found: %s"
        .formatted(secretsScanDetails.getRepoName(), secretsScanDetails.getName());
  }

  private static String getSummary(JobDetails jobDetails) {
    return "[%s] Run failed: %s::%s"
        .formatted(jobDetails.getRepoName(), jobDetails.getWorkflowName(), jobDetails.getName());
  }

  private static String getSummary(CodeStandardViolationDetails codeStandardViolationDetails) {
    return "[%s] Code standard violation found: %s"
        .formatted(
            codeStandardViolationDetails.getRepoName(), codeStandardViolationDetails.getName());
  }
}
