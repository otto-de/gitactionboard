package de.otto.platform.gitactionboard.adapters.service.branches;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class BranchesResponse {

  @NonNull String name;
}
