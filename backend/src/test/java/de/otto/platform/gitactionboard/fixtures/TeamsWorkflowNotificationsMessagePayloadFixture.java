package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.adapters.service.notifications.TeamsWorkflowNotificationMessagePayload.Fact;
import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.CODE_STANDARD_VIOLATION_NAME;
import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.CODE_STANDARD_VIOLATION_URL;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.JOB_NAME;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.JOB_URL;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.WORKFLOW_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_URL;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import de.otto.platform.gitactionboard.adapters.service.notifications.TeamsWorkflowNotificationMessagePayload;
import java.util.List;

public class TeamsWorkflowNotificationsMessagePayloadFixture {

  public static TeamsWorkflowNotificationMessagePayload
          .TeamsWorkflowNotificationMessagePayloadBuilder
      getTeamsNotificationPayloadBuilderForSecret() {
    final String summary =
        "[%s] Exposed secret found: %s".formatted(REPO_NAME, SECRETS_SCAN_DETAILS_NAME);
    final List<Fact> facts =
        List.of(
            Fact.builder().title("Repository:").value(REPO_NAME).build(),
            Fact.builder().title("Secret Type:").value(SECRETS_SCAN_DETAILS_NAME).build());
    return getTeamsNotificationMessagePayloadBuilder(summary, facts, SECRETS_SCAN_DETAILS_URL);
  }

  public static TeamsWorkflowNotificationMessagePayload
          .TeamsWorkflowNotificationMessagePayloadBuilder
      getTeamsNotificationPayloadBuilderForCodeStandardViolation() {
    final String summary =
        "[%s] Code standard violation found: %s".formatted(REPO_NAME, CODE_STANDARD_VIOLATION_NAME);
    final List<Fact> facts =
        List.of(
            Fact.builder().title("Repository:").value(REPO_NAME).build(),
            Fact.builder().title("Violation Type:").value(CODE_STANDARD_VIOLATION_NAME).build());
    return getTeamsNotificationMessagePayloadBuilder(summary, facts, CODE_STANDARD_VIOLATION_URL);
  }

  public static TeamsWorkflowNotificationMessagePayload
          .TeamsWorkflowNotificationMessagePayloadBuilder
      getTeamsNotificationPayloadBuilderForJob() {
    final String summary = "[%s] Run failed: %s::%s".formatted(REPO_NAME, WORKFLOW_NAME, JOB_NAME);

    final List<Fact> facts =
        List.of(
            Fact.builder().title("Repository:").value(REPO_NAME).build(),
            Fact.builder().title("Workflow Name:").value(WORKFLOW_NAME).build(),
            Fact.builder().title("Job Name:").value(JOB_NAME).build());

    return getTeamsNotificationMessagePayloadBuilder(summary, facts, JOB_URL);
  }

  private static TeamsWorkflowNotificationMessagePayload
          .TeamsWorkflowNotificationMessagePayloadBuilder
      getTeamsNotificationMessagePayloadBuilder(String summary, List<Fact> facts, String url) {
    return TeamsWorkflowNotificationMessagePayload.builder()
        .body(
            List.of(
                TeamsWorkflowNotificationMessagePayload.TextBlockBodyElement.builder()
                    .text(summary)
                    .build(),
                TeamsWorkflowNotificationMessagePayload.FactSetBodyElement.builder()
                    .facts(facts)
                    .build(),
                TeamsWorkflowNotificationMessagePayload.ActionSetBodyElement.from(url)));
  }
}
