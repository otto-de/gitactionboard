package de.otto.platform.gitactionboard.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Notification {
  String id;
  Boolean status;
  String connectorType;
}
