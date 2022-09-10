package de.otto.platform.gitactionboard.domain.service.notifications;

import de.otto.platform.gitactionboard.domain.Notification;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import java.util.AbstractMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseNotificationsService<T> {
  private final List<NotificationConnector> notificationConnectors;
  private final NotificationRepository notificationRepository;

  public void sendNotifications(List<T> alerts) {
    final CompletableFuture<?>[] completableFutures =
        getNotificationConnectorAndAlertsCombinations(alerts)
            .parallel()
            .map(entry -> sendNotificationIfRequired(entry.getValue(), entry.getKey()))
            .toArray(CompletableFuture[]::new);

    try {
      CompletableFuture.allOf(completableFutures).get();
    } catch (Exception exception) {
      log.warn("Unable to send notifications", exception);
    }
  }

  private CompletableFuture<Void> sendNotificationIfRequired(
      T alert, NotificationConnector notificationConnector) {
    return CompletableFuture.runAsync(
        () -> {
          if (notificationRepository.findStatusByIdAndType(
              getName(alert), notificationConnector.getType())) return;

          final Notification notification =
              Notification.builder()
                  .id(getName(alert))
                  .status(sendNotification(alert, notificationConnector))
                  .connectorType(notificationConnector.getType())
                  .build();

          notificationRepository.createOrUpdate(notification);
        });
  }

  private Boolean sendNotification(T alert, NotificationConnector notificationConnector) {
    try {
      notify(alert, notificationConnector);
      return true;
    } catch (Exception exception) {
      log.warn(
          "Unable to send notification to {} for {}",
          notificationConnector.getType(),
          getName(alert),
          exception);

      return false;
    }
  }

  protected abstract String getName(T alert);

  private Stream<AbstractMap.SimpleImmutableEntry<NotificationConnector, T>>
      getNotificationConnectorAndAlertsCombinations(List<T> alerts) {
    return alerts.stream()
        .flatMap(
            alert ->
                notificationConnectors.stream()
                    .map(
                        notificationConnector ->
                            new AbstractMap.SimpleImmutableEntry<>(notificationConnector, alert)));
  }

  protected abstract void notify(T alert, NotificationConnector notificationConnector);
}
