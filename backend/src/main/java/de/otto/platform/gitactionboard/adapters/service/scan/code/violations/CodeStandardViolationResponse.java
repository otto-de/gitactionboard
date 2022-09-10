package de.otto.platform.gitactionboard.adapters.service.scan.code.violations;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class CodeStandardViolationResponse {
  @JsonProperty("number")
  long id;

  @JsonProperty("created_at")
  @NonNull
  Instant createdAt;

  @JsonProperty("html_url")
  @NonNull
  String url;

  @JsonProperty("rule")
  @NonNull
  Rule rule;

  @JsonProperty("most_recent_instance")
  @NonNull
  MostRecentInstance mostRecentInstance;

  @Value
  @Builder
  public static class Rule {
    @JsonProperty("description")
    @NonNull
    String description;
  }

  public CodeStandardViolationDetails toCodeStandardViolationDetails(String repoName) {
    return CodeStandardViolationDetails.builder()
        .id(id)
        .url(url)
        .createdAt(createdAt)
        .name(rule.getDescription())
        .location(mostRecentInstance.getFormattedLocation())
        .repoName(repoName)
        .build();
  }

  @Value
  @Builder
  public static class MostRecentInstance {
    @NonNull Location location;

    public String getFormattedLocation() {
      return location.getLocationPath();
    }
  }

  @Value
  @Builder
  public static class Location {
    @NonNull String path;

    @JsonProperty("start_line")
    @NonNull
    Integer startLine;

    @JsonProperty("end_line")
    @NonNull
    Integer endLine;

    public String getLocationPath() {
      final String fileName = path.substring(path.lastIndexOf('/') + 1);

      return "%s:%d-%d".formatted(fileName, startLine, endLine);
    }
  }
}
