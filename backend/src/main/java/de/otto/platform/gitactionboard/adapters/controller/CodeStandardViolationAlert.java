package de.otto.platform.gitactionboard.adapters.controller;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class CodeStandardViolationAlert {
  @NonNull String id;

  @NonNull String name;

  @NonNull String url;

  @NonNull Instant createdAt;

  public static CodeStandardViolationAlert from(
      CodeStandardViolationDetails codeStandardViolationDetails) {
    return CodeStandardViolationAlert.builder()
        .id(codeStandardViolationDetails.getFormattedName())
        .name(
            String.join(
                " :: ",
                codeStandardViolationDetails.getRepoName(),
                codeStandardViolationDetails.getLocation(),
                codeStandardViolationDetails.getName()))
        .createdAt(codeStandardViolationDetails.getCreatedAt())
        .url(codeStandardViolationDetails.getUrl())
        .build();
  }
}
