package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.Fact;
import static de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.PotentialAction;
import static de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.Section;
import static de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.Target;
import static de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.TeamsNotificationMessagePayloadBuilder;
import static de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.builder;
import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.CODE_STANDARD_VIOLATION_NAME;
import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.CODE_STANDARD_VIOLATION_URL;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.JOB_NAME;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.JOB_URL;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.WORKFLOW_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_URL;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import java.util.List;

public class TeamsNotificationsMessagePayloadFixture {
  private static final String RED_COLOR = "c5413a";

  public static TeamsNotificationMessagePayloadBuilder
      getTeamsNotificationPayloadBuilderForSecret() {
    final String summary =
        "[%s] Exposed secret found: %s".formatted(REPO_NAME, SECRETS_SCAN_DETAILS_NAME);
    final List<Fact> facts =
        List.of(
            Fact.builder().name("Repository:").value(REPO_NAME).build(),
            Fact.builder().name("Secret Type:").value(SECRETS_SCAN_DETAILS_NAME).build());
    return getTeamsNotificationMessagePayloadBuilder(summary, facts, SECRETS_SCAN_DETAILS_URL);
  }

  public static TeamsNotificationMessagePayloadBuilder
      getTeamsNotificationPayloadBuilderForCodeStandardViolation() {
    final String summary =
        "[%s] Code standard violation found: %s".formatted(REPO_NAME, CODE_STANDARD_VIOLATION_NAME);
    final List<Fact> facts =
        List.of(
            Fact.builder().name("Repository:").value(REPO_NAME).build(),
            Fact.builder().name("Violation Type:").value(CODE_STANDARD_VIOLATION_NAME).build());
    return getTeamsNotificationMessagePayloadBuilder(summary, facts, CODE_STANDARD_VIOLATION_URL);
  }

  public static TeamsNotificationMessagePayloadBuilder getTeamsNotificationPayloadBuilderForJob() {
    final String summary = "[%s] Run failed: %s::%s".formatted(REPO_NAME, WORKFLOW_NAME, JOB_NAME);

    final List<Fact> facts =
        List.of(
            Fact.builder().name("Repository:").value(REPO_NAME).build(),
            Fact.builder().name("Workflow Name:").value(WORKFLOW_NAME).build(),
            Fact.builder().name("Job Name:").value(JOB_NAME).build());

    return getTeamsNotificationMessagePayloadBuilder(summary, facts, JOB_URL);
  }

  private static TeamsNotificationMessagePayloadBuilder getTeamsNotificationMessagePayloadBuilder(
      String summary, List<Fact> facts, String url) {
    return builder()
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
                    .build()));
  }
}
