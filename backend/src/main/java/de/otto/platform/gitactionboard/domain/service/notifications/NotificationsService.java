package de.otto.platform.gitactionboard.domain.service.notifications;

import de.otto.platform.gitactionboard.domain.Notification;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.workflow.Activity;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.Status;
import java.util.AbstractMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsService {
  private final List<NotificationConnector> notificationConnectors;
  private final NotificationRepository notificationRepository;

  public void sendNotificationsForWorkflowJobs(List<JobDetails> jobs) {
    final CompletableFuture<?>[] completableFutures =
        getNotificationConnectorAndJobsCombinations(jobs)
            .parallel()
            .map(entry -> sendNotifications((JobDetails) entry.getValue(), entry.getKey()))
            .toArray(CompletableFuture[]::new);

    sendNotifications(completableFutures);
  }

  public void sendNotificationsForSecretScanAlerts(List<SecretsScanDetails> secretsScanAlerts) {
    final CompletableFuture<?>[] completableFutures =
        getNotificationConnectorAndJobsCombinations(secretsScanAlerts)
            .parallel()
            .map(entry -> sendNotifications((SecretsScanDetails) entry.getValue(), entry.getKey()))
            .toArray(CompletableFuture[]::new);

    sendNotifications(completableFutures);
  }

  private void sendNotifications(CompletableFuture<?>... completableFutures) {
    try {
      CompletableFuture.allOf(completableFutures).get();
    } catch (Exception exception) {
      log.warn("Unable to send notifications", exception);
    }
  }

  private CompletableFuture<Void> sendNotifications(
      JobDetails jobDetails, NotificationConnector notificationConnector) {

    return CompletableFuture.runAsync(
        () -> {
          if (!isNeedNotify(jobDetails, notificationConnector)) return;

          final Notification notification =
              Notification.builder()
                  .id(jobDetails.getFormattedName())
                  .status(sendNotification(jobDetails, notificationConnector))
                  .connectorType(notificationConnector.getType())
                  .build();

          notificationRepository.createOrUpdate(notification);
        });
  }

  private CompletableFuture<Void> sendNotifications(
      SecretsScanDetails secretsScanDetails, NotificationConnector notificationConnector) {
    return CompletableFuture.runAsync(
        () -> {
          if (notificationRepository.findStatusByIdAndType(
              secretsScanDetails.getFormattedName(), notificationConnector.getType())) return;

          final Notification notification =
              Notification.builder()
                  .id(secretsScanDetails.getFormattedName())
                  .status(sendNotification(secretsScanDetails, notificationConnector))
                  .connectorType(notificationConnector.getType())
                  .build();

          notificationRepository.createOrUpdate(notification);
        });
  }

  private Stream<AbstractMap.SimpleImmutableEntry<NotificationConnector, ?>>
      getNotificationConnectorAndJobsCombinations(List<?> jobs) {
    return jobs.stream()
        .flatMap(
            jobDetails ->
                notificationConnectors.stream()
                    .map(
                        notificationConnector ->
                            new AbstractMap.SimpleImmutableEntry<>(
                                notificationConnector, jobDetails)));
  }

  private Boolean sendNotification(
      SecretsScanDetails secretsScanDetails, NotificationConnector notificationConnector) {
    try {
      notificationConnector.notify(secretsScanDetails);
      return true;
    } catch (Exception exception) {
      log.warn(
          "Unable to send notification to {} for secret scan alert with name {}",
          notificationConnector.getType(),
          secretsScanDetails.getName(),
          exception);

      return false;
    }
  }

  private Boolean sendNotification(
      JobDetails jobDetails, NotificationConnector notificationConnector) {
    try {
      notificationConnector.notify(jobDetails);
      return true;
    } catch (Exception exception) {
      log.warn(
          "Unable to send notification to {} for job with name {}",
          notificationConnector.getType(),
          jobDetails.getFormattedName(),
          exception);

      return false;
    }
  }

  private boolean isNeedNotify(JobDetails jobDetails, NotificationConnector notificationConnector) {
    return isNotificationRequired(jobDetails)
        && !notificationRepository.findStatusByIdAndType(
            jobDetails.getFormattedName(), notificationConnector.getType());
  }

  private boolean isNotificationRequired(JobDetails jobDetails) {
    return Activity.SLEEPING.equals(jobDetails.getActivity())
        && Status.FAILURE.equals(jobDetails.getLastBuildStatus());
  }
}
