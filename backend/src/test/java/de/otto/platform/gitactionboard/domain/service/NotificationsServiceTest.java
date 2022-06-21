package de.otto.platform.gitactionboard.domain.service;

import static de.otto.platform.gitactionboard.fixtures.JobDetailsFixture.getJobDetailsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.domain.Activity;
import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.Notification;
import de.otto.platform.gitactionboard.domain.Status;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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

  @Test
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
  void shouldSendNotificationsForGivenJobs() {
    when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
    when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);
    doNothing().when(notificationConnector1).notify(any(JobDetails.class));
    doNothing().when(notificationConnector2).notify(any(JobDetails.class));

    final JobDetails job1 =
        getJobDetailsBuilder().lastBuildStatus(Status.FAILURE).activity(Activity.SLEEPING).build();
    final JobDetails job2 =
        getJobDetailsBuilder()
            .lastBuildStatus(Status.FAILURE)
            .activity(Activity.SLEEPING)
            .name("test")
            .build();

    notificationsService.sendNotifications(List.of(job1, job2));

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
        getJobDetailsBuilder().lastBuildStatus(Status.FAILURE).activity(activity).build();

    notificationsService.sendNotifications(List.of(job));

    verifyNoInteractions(notificationConnector1, notificationConnector2, notificationRepository);
  }

  @ParameterizedTest
  @EnumSource(
      value = Status.class,
      names = {"FAILURE"},
      mode = EnumSource.Mode.EXCLUDE)
  void shouldNotSendNotificationIfBuildStatusOtherThanFailure(Status lastBuildStatus) {
    final JobDetails job =
        getJobDetailsBuilder().lastBuildStatus(lastBuildStatus).activity(Activity.SLEEPING).build();

    notificationsService.sendNotifications(List.of(job));

    verifyNoInteractions(notificationConnector1, notificationConnector2, notificationRepository);
  }

  @Test
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
  void shouldNotSendNotificationIfNotificationIsAlreadySent() {
    final JobDetails job =
        getJobDetailsBuilder().lastBuildStatus(Status.FAILURE).activity(Activity.SLEEPING).build();

    when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
    when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);

    doNothing().when(notificationConnector2).notify(any(JobDetails.class));

    when(notificationRepository.findStatusByIdAndType(job.getFormattedName(), CONNECTOR_TYPE_1))
        .thenReturn(true);
    when(notificationRepository.findStatusByIdAndType(job.getFormattedName(), CONNECTOR_TYPE_2))
        .thenReturn(false);

    notificationsService.sendNotifications(List.of(job));

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
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
  void shouldNotThrowErrorIfUnableToSendNotification() {
    final JobDetails job =
        getJobDetailsBuilder().lastBuildStatus(Status.FAILURE).activity(Activity.SLEEPING).build();

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

    notificationsService.sendNotifications(List.of(job));

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
