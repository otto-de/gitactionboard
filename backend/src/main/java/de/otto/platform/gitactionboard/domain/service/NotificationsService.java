package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.Activity;
import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.Notification;
import de.otto.platform.gitactionboard.domain.Status;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
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

  public void sendNotifications(List<JobDetails> jobs) {
    final CompletableFuture<?>[] completableFutures =
        getNotificationConnectorAndJobsCombinations(jobs)
            .parallel()
            .map(entry -> sendNotifications(entry.getValue(), entry.getKey()))
            .toArray(CompletableFuture[]::new);

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

  private Stream<AbstractMap.SimpleImmutableEntry<NotificationConnector, JobDetails>>
      getNotificationConnectorAndJobsCombinations(List<JobDetails> jobs) {
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
