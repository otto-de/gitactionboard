package de.otto.platform.gitactionboard.adapters.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Sequential
class InMemoryNotificationRepositoryTest {

  private static final String CONNECTOR_TYPE = "TEST";
  private static final String ID = "TestId";

  private InMemoryNotificationRepository notificationRepository;

  @BeforeEach
  void setUp() {
    notificationRepository = new InMemoryNotificationRepository();
  }

  @ParameterizedTest
  @CsvSource(value = {"false", "true"})
  void shouldStoreNotification(Boolean status) {
    final Notification notification =
        Notification.builder().connectorType(CONNECTOR_TYPE).status(status).id(ID).build();

    notificationRepository.createOrUpdate(notification);

    assertThat(notificationRepository.findStatusByIdAndType(ID, CONNECTOR_TYPE)).isEqualTo(status);
  }

  @Test
  void shouldGracefullyUpdateExistingRecord() {

    notificationRepository.createOrUpdate(
        Notification.builder().connectorType(CONNECTOR_TYPE).status(false).id(ID).build());

    assertThat(notificationRepository.findStatusByIdAndType(ID, CONNECTOR_TYPE)).isFalse();

    notificationRepository.createOrUpdate(
        Notification.builder().connectorType(CONNECTOR_TYPE).status(true).id(ID).build());

    assertThat(notificationRepository.findStatusByIdAndType(ID, CONNECTOR_TYPE)).isTrue();
  }

  @Nested
  class FindJobStatusByIdAndType {
    @Test
    void shouldReturnDefaultStatusWhenStoreIsEmpty() {
      assertThat(notificationRepository.findStatusByIdAndType(ID, CONNECTOR_TYPE)).isFalse();
    }

    @ParameterizedTest
    @CsvSource(
        value = {
          "TestId, TEST, true, TestId, test, false",
          "TestId, TEST, true, testId, TEST, false",
          "TestId, TEST, true, TestId, dummy, false",
          "TestId, TEST, true, dummy, TEST, false",
          "TestId, TEST, true, TestId, TEST, true",
        })
    void shouldReturnStatusForGivenIdAndType(
        String existingId,
        String existingType,
        Boolean existingStatus,
        String enquireId,
        String enquireType,
        Boolean expectedStatus) {
      notificationRepository.createOrUpdate(
          Notification.builder()
              .id(existingId)
              .connectorType(existingType)
              .status(existingStatus)
              .build());

      assertThat(notificationRepository.findStatusByIdAndType(enquireId, enquireType))
          .isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @CsvSource(
        value = {
          "TestId, TEST, true",
          "TestId, DUMMY, false",
          "dummy, TEST, true",
          "TestId, ANOTHER_TYPE, false",
        })
    void shouldReturnStatusForGivenIdAndTypeWhenStoreHasMultipleItems(
        String enquireId, String enquireType, Boolean expectedStatus) {
      notificationRepository.createOrUpdate(
          Notification.builder().id(ID).connectorType(CONNECTOR_TYPE).status(true).build());
      notificationRepository.createOrUpdate(
          Notification.builder().id("dummy").connectorType(CONNECTOR_TYPE).status(true).build());
      notificationRepository.createOrUpdate(
          Notification.builder().id("skeleton").connectorType(CONNECTOR_TYPE).status(true).build());
      notificationRepository.createOrUpdate(
          Notification.builder().id(ID).connectorType("ANOTHER_TYPE").status(false).build());
      notificationRepository.createOrUpdate(
          Notification.builder().id("dummy").connectorType("ANOTHER_TYPE").status(true).build());
      notificationRepository.createOrUpdate(
          Notification.builder().id("skeleton").connectorType("ANOTHER_TYPE").status(true).build());

      assertThat(notificationRepository.findStatusByIdAndType(enquireId, enquireType))
          .isEqualTo(expectedStatus);
    }
  }
}
