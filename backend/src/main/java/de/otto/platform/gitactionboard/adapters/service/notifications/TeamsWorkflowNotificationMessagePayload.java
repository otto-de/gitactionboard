package de.otto.platform.gitactionboard.adapters.service.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class TeamsWorkflowNotificationMessagePayload {
  private static final String REPOSITORY_FACT_NAME = "Repository:";
  private static final String SECRET_TYPE_FACT_NAME = "Secret Type:";
  private static final String VIOLATION_TYPE_FACT_NAME = "Violation Type:";
  private static final String WORKFLOW_FACT_NAME = "Workflow Name:";
  private static final String JOB_FACT_NAME = "Job Name:";
  private static final String ADAPTIVE_CARD_TYPE = "AdaptiveCard";
  private static final String CARD_SCHEMA_URL =
      "https://adaptivecards.io/schemas/adaptive-card.json";
  private static final String CARD_SCHEMA_VERSION = "1.4";
  private static final Map<String, String> MS_TEAMS_METADATA = Map.of("width", "Full");

  String type = ADAPTIVE_CARD_TYPE;
  List<BodyElement> body;

  @JsonProperty("$schema")
  String schema = CARD_SCHEMA_URL;

  String version = CARD_SCHEMA_VERSION;
  Map<String, String> msteams = MS_TEAMS_METADATA;

  @FunctionalInterface
  interface BodyElement {
    String getType();
  }

  @Builder
  @Value
  public static class TextBlockBodyElement implements BodyElement {
    private static final String ATTENTION = "Attention";
    private static final String WEIGHT_BOLDER = "Bolder";
    private static final String TEXT_BLOCK_TYPE = "TextBlock";

    String type = TEXT_BLOCK_TYPE;
    String weight = WEIGHT_BOLDER;
    String text;
    boolean wrap = true;
    String color = ATTENTION;

    public static TextBlockBodyElement from(String summary) {
      return TextBlockBodyElement.builder().text(summary).build();
    }
  }

  @Builder
  @Value
  public static class FactSetBodyElement implements BodyElement {
    private static final String FACT_SET_TYPE = "FactSet";

    String type = FACT_SET_TYPE;
    List<Fact> facts;

    public static FactSetBodyElement from(List<Fact> facts) {
      return FactSetBodyElement.builder().facts(facts).build();
    }
  }

  @Builder
  @Value
  public static class ActionSetBodyElement implements BodyElement {
    private static final String ACTION_SET_TYPE = "ActionSet";

    String type = ACTION_SET_TYPE;
    List<OpenUrlAction> actions;

    public static ActionSetBodyElement from(@NonNull String url) {
      return ActionSetBodyElement.builder().actions(List.of(OpenUrlAction.from(url))).build();
    }
  }

  @Builder
  @Value
  public static class Fact {
    String title;
    String value;
  }

  @Builder
  @Value
  static class OpenUrlAction {
    private static final String OPEN_URL_ACTION_TYPE = "Action.OpenUrl";
    private static final String VIEW_IN_GITHUB_TITLE_TEXT = "View in GitHub";

    String type = OPEN_URL_ACTION_TYPE;
    String title = VIEW_IN_GITHUB_TITLE_TEXT;
    String url;

    public static OpenUrlAction from(@NonNull String url) {
      return OpenUrlAction.builder().url(url).build();
    }
  }

  public static TeamsWorkflowNotificationMessagePayload from(JobDetails jobDetails) {
    return buildMessagePayload(getSummary(jobDetails), jobDetails.getUrl(), buildFacts(jobDetails));
  }

  public static TeamsWorkflowNotificationMessagePayload from(
      SecretsScanDetails secretsScanDetails) {
    return buildMessagePayload(
        getSummary(secretsScanDetails),
        secretsScanDetails.getUrl(),
        buildFacts(secretsScanDetails));
  }

  public static TeamsWorkflowNotificationMessagePayload from(
      CodeStandardViolationDetails codeStandardViolationDetails) {
    return buildMessagePayload(
        getSummary(codeStandardViolationDetails),
        codeStandardViolationDetails.getUrl(),
        buildFacts(codeStandardViolationDetails));
  }

  private static TeamsWorkflowNotificationMessagePayload buildMessagePayload(
      String summary,
      @NonNull String url,
      List<TeamsWorkflowNotificationMessagePayload.Fact> facts) {
    return TeamsWorkflowNotificationMessagePayload.builder()
        .body(
            List.of(
                TextBlockBodyElement.from(summary),
                FactSetBodyElement.from(facts),
                ActionSetBodyElement.from(url)))
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

  private static List<TeamsWorkflowNotificationMessagePayload.Fact> buildFacts(
      SecretsScanDetails secretsScanDetails) {
    return List.of(
        buildFact(REPOSITORY_FACT_NAME, secretsScanDetails.getRepoName()),
        buildFact(SECRET_TYPE_FACT_NAME, secretsScanDetails.getName()));
  }

  private static List<TeamsWorkflowNotificationMessagePayload.Fact> buildFacts(
      CodeStandardViolationDetails codeStandardViolationDetails) {
    return List.of(
        buildFact(REPOSITORY_FACT_NAME, codeStandardViolationDetails.getRepoName()),
        buildFact(VIOLATION_TYPE_FACT_NAME, codeStandardViolationDetails.getName()));
  }

  private static List<TeamsWorkflowNotificationMessagePayload.Fact> buildFacts(
      JobDetails jobDetails) {
    return List.of(
        buildFact(REPOSITORY_FACT_NAME, jobDetails.getRepoName()),
        buildFact(WORKFLOW_FACT_NAME, jobDetails.getWorkflowName()),
        buildFact(JOB_FACT_NAME, jobDetails.getName()));
  }

  private static TeamsWorkflowNotificationMessagePayload.Fact buildFact(String name, String value) {
    return Fact.builder().title(name).value(value).build();
  }
}
