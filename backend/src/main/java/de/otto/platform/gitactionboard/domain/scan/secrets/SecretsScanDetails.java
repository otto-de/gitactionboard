package de.otto.platform.gitactionboard.domain.scan.secrets;

import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SecretsScanDetails {
  long id;

  @NonNull String name;

  @NonNull String url;

  @NonNull Instant createdAt;

  @NonNull String repoName;

  public String getFormattedName() {
    return "%s::%s::%d".formatted(repoName, name, id);
  }
}
