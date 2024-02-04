package de.otto.platform.gitactionboard.adapters.repository;

import de.otto.platform.gitactionboard.domain.Notification;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryNotificationRepository implements NotificationRepository {

  private final Map<String, Notification> store;

  public InMemoryNotificationRepository() {
    store = new HashMap<>();
  }

  @Override
  public void createOrUpdate(Notification notification) {
    store.put(createId(notification.getId(), notification.getConnectorType()), notification);
  }

  @Override
  public void deleteAll() {
    store.clear();
  }

  private String createId(String id, String connectorType) {
    return "%s_%s".formatted(id, connectorType);
  }

  @Override
  public boolean findStatusByIdAndType(String id, String type) {
    return Optional.ofNullable(store.get(createId(id, type)))
        .map(Notification::getStatus)
        .orElse(false);
  }
}
