package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.JOB_NAME;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.JOB_URL;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.WORKFLOW_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload;
import de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.Fact;
import de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.PotentialAction;
import de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.Section;
import de.otto.platform.gitactionboard.adapters.service.notifications.TeamsNotificationMessagePayload.Target;
import java.util.List;

public class NotificationMessagePayloadFixture {

  public static TeamsNotificationMessagePayload.TeamsNotificationMessagePayloadBuilder
      getNotificationMessagePayloadBuilder() {
    final String summary = "[%s] Run failed: %s::%s".formatted(REPO_NAME, WORKFLOW_NAME, JOB_NAME);

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
                            Fact.builder().name("Repository:").value(REPO_NAME).build(),
                            Fact.builder().name("Workflow Name:").value(WORKFLOW_NAME).build(),
                            Fact.builder().name("Job Name:").value(JOB_NAME).build()))
                    .build()))
        .potentialAction(
            List.of(
                PotentialAction.builder()
                    .type("OpenUri")
                    .name("View in GitHub")
                    .targets(List.of(Target.builder().os("default").uri(JOB_URL).build()))
                    .build()));
  }
}
