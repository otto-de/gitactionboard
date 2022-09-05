package de.otto.platform.gitactionboard.adapters.controller;

import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SecretsScanAlert {
  @NonNull String id;

  @NonNull String name;

  @NonNull String url;

  @NonNull Instant createdAt;

  public static SecretsScanAlert from(SecretsScanDetails secretsScanDetails) {
    return SecretsScanAlert.builder()
        .id(secretsScanDetails.getFormattedName())
        .name(String.join(" :: ", secretsScanDetails.getRepoName(), secretsScanDetails.getName()))
        .createdAt(secretsScanDetails.getCreatedAt())
        .url(secretsScanDetails.getUrl())
        .build();
  }
}
