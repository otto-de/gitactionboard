package de.otto.platform.gitactionboard.domain.workflow;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Workflow {
  @NonNull String name;
  @NonNull String repoName;
  long id;
}
