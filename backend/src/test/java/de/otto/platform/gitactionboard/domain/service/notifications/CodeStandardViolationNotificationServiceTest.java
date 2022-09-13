package de.otto.platform.gitactionboard.domain.service.notifications;

import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.getCodeStandardViolationDetailsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.Notification;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
@Sequential
class CodeStandardViolationNotificationServiceTest {
  @Mock private NotificationRepository notificationRepository;
  @Mock private NotificationConnector notificationConnector1;
  @Mock private NotificationConnector notificationConnector2;

  private static final String CONNECTOR_TYPE_1 = "CONNECTOR_1";
  private static final String CONNECTOR_TYPE_2 = "CONNECTOR_2";

  private CodeStandardViolationNotificationService notificationsService;

  @BeforeEach
  void setUp() {
    notificationsService =
        new CodeStandardViolationNotificationService(
            List.of(notificationConnector1, notificationConnector2), notificationRepository);
  }

  @Test
  void shouldSendNotificationsForGivenCodeStandardViolation() {
    when(notificationRepository.findStatusByIdAndType(anyString(), anyString())).thenReturn(false);
    when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
    when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);
    doNothing().when(notificationConnector1).notify(any(CodeStandardViolationDetails.class));
    doNothing().when(notificationConnector2).notify(any(CodeStandardViolationDetails.class));

    final CodeStandardViolationDetails violation1 =
        getCodeStandardViolationDetailsBuilder().build();
    final CodeStandardViolationDetails violation2 =
        getCodeStandardViolationDetailsBuilder().id(2L).build();

    notificationsService.sendNotifications(List.of(violation1, violation2));

    verify(notificationConnector1, times(1)).notify(violation1);
    verify(notificationConnector1, times(1)).notify(violation2);
    verify(notificationConnector1, atLeastOnce()).getType();

    verify(notificationConnector2, times(1)).notify(violation1);
    verify(notificationConnector2, times(1)).notify(violation2);
    verify(notificationConnector1, atLeastOnce()).getType();

    final ArgumentCaptor<Notification> notificationArgumentCaptor =
        ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository, times(4)).createOrUpdate(notificationArgumentCaptor.capture());

    assertThat(notificationArgumentCaptor.getAllValues())
        .containsExactlyInAnyOrder(
            Notification.builder()
                .status(true)
                .connectorType(CONNECTOR_TYPE_1)
                .id(violation1.getFormattedName())
                .build(),
            Notification.builder()
                .status(true)
                .connectorType(CONNECTOR_TYPE_1)
                .id(violation2.getFormattedName())
                .build(),
            Notification.builder()
                .status(true)
                .connectorType(CONNECTOR_TYPE_2)
                .id(violation1.getFormattedName())
                .build(),
            Notification.builder()
                .status(true)
                .connectorType(CONNECTOR_TYPE_2)
                .id(violation2.getFormattedName())
                .build());
  }

  @Test
  void shouldNotSendNotificationIfNotificationIsAlreadySent() {
    when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
    when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);
    doNothing().when(notificationConnector2).notify(any(CodeStandardViolationDetails.class));

    final CodeStandardViolationDetails violation1 =
        getCodeStandardViolationDetailsBuilder().build();

    when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
    when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);

    when(notificationRepository.findStatusByIdAndType(
            violation1.getFormattedName(), CONNECTOR_TYPE_1))
        .thenReturn(true);
    when(notificationRepository.findStatusByIdAndType(
            violation1.getFormattedName(), CONNECTOR_TYPE_2))
        .thenReturn(false);

    notificationsService.sendNotifications(List.of(violation1));

    verify(notificationConnector1, never()).notify(any(CodeStandardViolationDetails.class));
    verify(notificationConnector2, times(1)).notify(violation1);

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
                .id(violation1.getFormattedName())
                .build());
  }

  @Test
  void shouldNotThrowErrorIfUnableToSendNotification() {
    when(notificationConnector1.getType()).thenReturn(CONNECTOR_TYPE_1);
    when(notificationConnector2.getType()).thenReturn(CONNECTOR_TYPE_2);
    when(notificationRepository.findStatusByIdAndType(anyString(), anyString())).thenReturn(false);

    doNothing().when(notificationConnector2).notify(any(CodeStandardViolationDetails.class));

    doThrow(new RuntimeException("Boom"))
        .when(notificationConnector1)
        .notify(any(CodeStandardViolationDetails.class));

    final CodeStandardViolationDetails violation1 =
        getCodeStandardViolationDetailsBuilder().build();

    notificationsService.sendNotifications(List.of(violation1));

    verify(notificationConnector1, times(1)).notify(violation1);
    verify(notificationConnector2, times(1)).notify(violation1);

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
                .id(violation1.getFormattedName())
                .build(),
            Notification.builder()
                .status(true)
                .connectorType(CONNECTOR_TYPE_2)
                .id(violation1.getFormattedName())
                .build());
  }
}
