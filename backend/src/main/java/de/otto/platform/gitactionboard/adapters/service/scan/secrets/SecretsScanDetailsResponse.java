package de.otto.platform.gitactionboard.adapters.service.scan.secrets;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SecretsScanDetailsResponse {
  @JsonProperty("number")
  long id;

  @JsonProperty("created_at")
  @NonNull
  Instant createdAt;

  @JsonProperty("html_url")
  @NonNull
  String url;

  @JsonProperty("secret_type_display_name")
  @NonNull
  String displayName;

  public SecretsScanDetails toSecretsScanDetails(String repoName) {
    return SecretsScanDetails.builder()
        .id(id)
        .url(url)
        .createdAt(createdAt)
        .name(displayName)
        .repoName(repoName)
        .build();
  }
}
