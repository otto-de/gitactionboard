package de.otto.platform.gitactionboard.domain.service.notifications;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.getJobDetailsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.Notification;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.workflow.Activity;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobStatus;
import de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
@Sequential
class NotificationsServiceTest {
  @Mock private NotificationRepository notificationRepository;
  @Mock private NotificationConnector notificationConnector1;
  @Mock private NotificationConnector notificationConnector2;

  private static final String CONNECTOR_TYPE_1 = "CONNECTOR_1";
  private static final String CONNECTOR_TYPE_2 = "CONNECTOR_2";

  private NotificationsService notificationsService;

  @BeforeEach
  void setUp() {
    notificationsService =
        new NotificationsService(
            List.of(notificationConnector1, notificationConnector2), notificationRepository);
  }

  @Nested
  class WorkflowNotifications {
    @Test
    void shouldSendNotificationsForGivenJobs() {
      when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
      when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);
      doNothing().when(notificationConnector1).notify(any(JobDetails.class));
      doNothing().when(notificationConnector2).notify(any(JobDetails.class));

      final JobDetails job1 =
          getJobDetailsBuilder()
              .lastBuildStatus(JobStatus.FAILURE)
              .activity(Activity.SLEEPING)
              .build();
      final JobDetails job2 =
          getJobDetailsBuilder()
              .lastBuildStatus(JobStatus.FAILURE)
              .activity(Activity.SLEEPING)
              .name("test")
              .build();

      notificationsService.sendNotificationsForWorkflowJobs(List.of(job1, job2));

      verify(notificationConnector1, times(1)).notify(job1);
      verify(notificationConnector1, times(1)).notify(job2);
      verify(notificationConnector1, atLeastOnce()).getType();

      verify(notificationConnector2, times(1)).notify(job1);
      verify(notificationConnector2, times(1)).notify(job2);
      verify(notificationConnector1, atLeastOnce()).getType();

      final ArgumentCaptor<Notification> notificationArgumentCaptor =
          ArgumentCaptor.forClass(Notification.class);
      verify(notificationRepository, times(4)).createOrUpdate(notificationArgumentCaptor.capture());

      assertThat(notificationArgumentCaptor.getAllValues())
          .containsExactlyInAnyOrder(
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_1)
                  .id(job1.getFormattedName())
                  .build(),
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_1)
                  .id(job2.getFormattedName())
                  .build(),
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_2)
                  .id(job1.getFormattedName())
                  .build(),
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_2)
                  .id(job2.getFormattedName())
                  .build());
    }

    @ParameterizedTest
    @EnumSource(
        value = Activity.class,
        names = {"SLEEPING"},
        mode = EnumSource.Mode.EXCLUDE)
    void shouldNotSendNotificationIfCurrentActivityStateOtherThanSleeping(Activity activity) {
      final JobDetails job =
          getJobDetailsBuilder().lastBuildStatus(JobStatus.FAILURE).activity(activity).build();

      notificationsService.sendNotificationsForWorkflowJobs(List.of(job));

      verifyNoInteractions(notificationConnector1, notificationConnector2, notificationRepository);
    }

    @ParameterizedTest
    @EnumSource(
        value = JobStatus.class,
        names = {"FAILURE"},
        mode = EnumSource.Mode.EXCLUDE)
    void shouldNotSendNotificationIfBuildStatusOtherThanFailure(JobStatus lastBuildJobStatus) {
      final JobDetails job =
          getJobDetailsBuilder()
              .lastBuildStatus(lastBuildJobStatus)
              .activity(Activity.SLEEPING)
              .build();

      notificationsService.sendNotificationsForWorkflowJobs(List.of(job));

      verifyNoInteractions(notificationConnector1, notificationConnector2, notificationRepository);
    }

    @Test
    void shouldNotSendNotificationIfNotificationIsAlreadySent() {
      final JobDetails job =
          getJobDetailsBuilder()
              .lastBuildStatus(JobStatus.FAILURE)
              .activity(Activity.SLEEPING)
              .build();

      when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
      when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);

      doNothing().when(notificationConnector2).notify(any(JobDetails.class));

      when(notificationRepository.findStatusByIdAndType(job.getFormattedName(), CONNECTOR_TYPE_1))
          .thenReturn(true);
      when(notificationRepository.findStatusByIdAndType(job.getFormattedName(), CONNECTOR_TYPE_2))
          .thenReturn(false);

      notificationsService.sendNotificationsForWorkflowJobs(List.of(job));

      verify(notificationConnector1, never()).notify(any(JobDetails.class));
      verify(notificationConnector2, times(1)).notify(job);

      verify(notificationConnector1, atLeastOnce()).getType();
      verify(notificationConnector2, atLeastOnce()).getType();

      final ArgumentCaptor<Notification> notificationArgumentCaptor =
          ArgumentCaptor.forClass(Notification.class);
      verify(notificationRepository, times(1)).createOrUpdate(notificationArgumentCaptor.capture());

      assertThat(notificationArgumentCaptor.getAllValues())
          .hasSize(1)
          .first()
          .isEqualTo(
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_2)
                  .id(job.getFormattedName())
                  .build());
    }

    @Test
    void shouldNotThrowErrorIfUnableToSendNotification() {
      final JobDetails job =
          getJobDetailsBuilder()
              .lastBuildStatus(JobStatus.FAILURE)
              .activity(Activity.SLEEPING)
              .build();

      when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
      when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);

      doThrow(new RuntimeException("Boom"))
          .when(notificationConnector1)
          .notify(any(JobDetails.class));

      doNothing().when(notificationConnector2).notify(any(JobDetails.class));

      when(notificationRepository.findStatusByIdAndType(job.getFormattedName(), CONNECTOR_TYPE_1))
          .thenReturn(false);
      when(notificationRepository.findStatusByIdAndType(job.getFormattedName(), CONNECTOR_TYPE_2))
          .thenReturn(false);

      notificationsService.sendNotificationsForWorkflowJobs(List.of(job));

      verify(notificationConnector1, times(1)).notify(job);
      verify(notificationConnector2, times(1)).notify(job);

      verify(notificationConnector1, atLeastOnce()).getType();
      verify(notificationConnector2, atLeastOnce()).getType();

      final ArgumentCaptor<Notification> notificationArgumentCaptor =
          ArgumentCaptor.forClass(Notification.class);
      verify(notificationRepository, times(2)).createOrUpdate(notificationArgumentCaptor.capture());

      assertThat(notificationArgumentCaptor.getAllValues())
          .containsExactlyInAnyOrder(
              Notification.builder()
                  .status(false)
                  .connectorType(CONNECTOR_TYPE_1)
                  .id(job.getFormattedName())
                  .build(),
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_2)
                  .id(job.getFormattedName())
                  .build());
    }
  }

  @Nested
  class SecretScanNotifications {
    @Test
    void shouldSendNotificationsForGivenSecrets() {
      when(notificationRepository.findStatusByIdAndType(anyString(), anyString()))
          .thenReturn(false);
      when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
      when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);
      doNothing().when(notificationConnector1).notify(any(SecretsScanDetails.class));
      doNothing().when(notificationConnector2).notify(any(SecretsScanDetails.class));

      final SecretsScanDetails secret1 =
          SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder().build();
      final SecretsScanDetails secret2 =
          SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder().id(2L).build();

      notificationsService.sendNotificationsForSecretScanAlerts(List.of(secret1, secret2));

      verify(notificationConnector1, times(1)).notify(secret1);
      verify(notificationConnector1, times(1)).notify(secret2);
      verify(notificationConnector1, atLeastOnce()).getType();

      verify(notificationConnector2, times(1)).notify(secret1);
      verify(notificationConnector2, times(1)).notify(secret2);
      verify(notificationConnector1, atLeastOnce()).getType();

      final ArgumentCaptor<Notification> notificationArgumentCaptor =
          ArgumentCaptor.forClass(Notification.class);
      verify(notificationRepository, times(4)).createOrUpdate(notificationArgumentCaptor.capture());

      assertThat(notificationArgumentCaptor.getAllValues())
          .containsExactlyInAnyOrder(
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_1)
                  .id(secret1.getFormattedName())
                  .build(),
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_1)
                  .id(secret2.getFormattedName())
                  .build(),
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_2)
                  .id(secret1.getFormattedName())
                  .build(),
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_2)
                  .id(secret2.getFormattedName())
                  .build());
    }

    @Test
    void shouldNotSendNotificationIfNotificationIsAlreadySent() {
      when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
      when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);
      doNothing().when(notificationConnector2).notify(any(SecretsScanDetails.class));

      final SecretsScanDetails secret1 =
          SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder().build();

      when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
      when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);

      when(notificationRepository.findStatusByIdAndType(
              secret1.getFormattedName(), CONNECTOR_TYPE_1))
          .thenReturn(true);
      when(notificationRepository.findStatusByIdAndType(
              secret1.getFormattedName(), CONNECTOR_TYPE_2))
          .thenReturn(false);

      notificationsService.sendNotificationsForSecretScanAlerts(List.of(secret1));

      verify(notificationConnector1, never()).notify(any(SecretsScanDetails.class));
      verify(notificationConnector2, times(1)).notify(secret1);

      verify(notificationConnector1, atLeastOnce()).getType();
      verify(notificationConnector2, atLeastOnce()).getType();

      final ArgumentCaptor<Notification> notificationArgumentCaptor =
          ArgumentCaptor.forClass(Notification.class);
      verify(notificationRepository, times(1)).createOrUpdate(notificationArgumentCaptor.capture());

      assertThat(notificationArgumentCaptor.getAllValues())
          .hasSize(1)
          .first()
          .isEqualTo(
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_2)
                  .id(secret1.getFormattedName())
                  .build());
    }

    @Test
    void shouldNotThrowErrorIfUnableToSendNotification() {
      when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
      when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);
      when(notificationRepository.findStatusByIdAndType(anyString(), anyString()))
          .thenReturn(false);

      doNothing().when(notificationConnector2).notify(any(SecretsScanDetails.class));

      doThrow(new RuntimeException("Boom"))
          .when(notificationConnector1)
          .notify(any(SecretsScanDetails.class));

      final SecretsScanDetails secret1 =
          SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder().build();

      notificationsService.sendNotificationsForSecretScanAlerts(List.of(secret1));

      verify(notificationConnector1, times(1)).notify(secret1);
      verify(notificationConnector2, times(1)).notify(secret1);

      verify(notificationConnector1, atLeastOnce()).getType();
      verify(notificationConnector2, atLeastOnce()).getType();

      final ArgumentCaptor<Notification> notificationArgumentCaptor =
          ArgumentCaptor.forClass(Notification.class);
      verify(notificationRepository, times(2)).createOrUpdate(notificationArgumentCaptor.capture());

      assertThat(notificationArgumentCaptor.getAllValues())
          .containsExactlyInAnyOrder(
              Notification.builder()
                  .status(false)
                  .connectorType(CONNECTOR_TYPE_1)
                  .id(secret1.getFormattedName())
                  .build(),
              Notification.builder()
                  .status(true)
                  .connectorType(CONNECTOR_TYPE_2)
                  .id(secret1.getFormattedName())
                  .build());
    }
  }
}
