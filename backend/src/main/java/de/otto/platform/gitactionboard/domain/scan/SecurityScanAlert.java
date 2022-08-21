package de.otto.platform.gitactionboard.domain.scan;

import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SecurityScanAlert {
  @NonNull String id;

  @NonNull String name;

  @NonNull String url;

  @NonNull Instant createdAt;

  public static SecurityScanAlert from(SecretsScanDetails secretsScanDetails) {
    return SecurityScanAlert.builder()
        .id(
            String.format(
                "%s::%s::%d",
                secretsScanDetails.getRepoName(),
                secretsScanDetails.getName(),
                secretsScanDetails.getId()))
        .name(String.join(" :: ", secretsScanDetails.getRepoName(), secretsScanDetails.getName()))
        .createdAt(secretsScanDetails.getCreatedAt())
        .url(secretsScanDetails.getUrl())
        .build();
  }
}
