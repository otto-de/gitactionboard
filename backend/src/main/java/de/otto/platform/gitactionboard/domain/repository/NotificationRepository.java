package de.otto.platform.gitactionboard.domain.repository;

import de.otto.platform.gitactionboard.domain.Notification;

public interface NotificationRepository {

  boolean findStatusByIdAndType(String id, String type);

  void createOrUpdate(Notification notification);

  void deleteAll();
}
