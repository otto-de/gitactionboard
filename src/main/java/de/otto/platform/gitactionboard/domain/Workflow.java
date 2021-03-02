package de.otto.platform.gitactionboard.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Workflow {
  @NonNull String name;
  @NonNull String repoName;
  int id;
}
