package de.otto.platform.gitactionboard.adapters.service.scan.secrets;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
