package de.otto.platform.gitactionboard.adapters.service.workflow;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class WorkflowsResponse {
  @NonNull List<WorkflowIdentifier> workflows;

  @Value
  @Builder
  public static class WorkflowIdentifier {
    long id;
    @NonNull String name;
    @NonNull String path;
    @NonNull String state;

    public boolean isActive() {
      return "active".equals(state) && path.startsWith(".github");
    }
  }
}
