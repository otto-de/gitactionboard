package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import de.otto.platform.gitactionboard.adapters.controller.CodeStandardViolationAlert;
import de.otto.platform.gitactionboard.adapters.service.scan.code.violations.CodeStandardViolationResponse;
import de.otto.platform.gitactionboard.adapters.service.scan.code.violations.CodeStandardViolationResponse.MostRecentInstance;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import java.time.Instant;

public class CodeStandardViolationFixture {
  public static final Long CODE_STANDARD_VIOLATION_ID = 1L;
  public static final String CODE_STANDARD_VIOLATION_URL =
      "https://github.com/johndoe/hello-world/code-scanning/alerts/1";
  public static final String CODE_STANDARD_VIOLATION_NAME =
      "Arbitrary file write during zip extraction";
  public static final Instant CODE_STANDARD_VIOLATION_CREATED_AT =
      Instant.parse("2021-10-18T06:14:54.000Z");
  public static final String VIOLATION_LOCATION = "dummy.java:2-2";

  public static CodeStandardViolationDetails.CodeStandardViolationDetailsBuilder
      getCodeStandardViolationDetailsBuilder() {
    return CodeStandardViolationDetails.builder()
        .id(CODE_STANDARD_VIOLATION_ID)
        .name(CODE_STANDARD_VIOLATION_NAME)
        .repoName(REPO_NAME)
        .url(CODE_STANDARD_VIOLATION_URL)
        .location(VIOLATION_LOCATION)
        .createdAt(CODE_STANDARD_VIOLATION_CREATED_AT);
  }

  public static CodeStandardViolationResponse.CodeStandardViolationResponseBuilder
      getCodeStandardViolationResponseBuilder() {
    return CodeStandardViolationResponse.builder()
        .id(CODE_STANDARD_VIOLATION_ID)
        .rule(
            CodeStandardViolationResponse.Rule.builder()
                .description(CODE_STANDARD_VIOLATION_NAME)
                .build())
        .mostRecentInstance(
            MostRecentInstance.builder()
                .location(
                    CodeStandardViolationResponse.Location.builder()
                        .path("test/dummy.java")
                        .startLine(2)
                        .endLine(2)
                        .build())
                .build())
        .url(CODE_STANDARD_VIOLATION_URL)
        .createdAt(CODE_STANDARD_VIOLATION_CREATED_AT);
  }

  public static CodeStandardViolationAlert.CodeStandardViolationAlertBuilder
      getCodeStandardViolationAlertBuilder() {
    return CodeStandardViolationAlert.builder()
        .id(
            "%s::%s::%d"
                .formatted(REPO_NAME, CODE_STANDARD_VIOLATION_NAME, CODE_STANDARD_VIOLATION_ID))
        .url(CODE_STANDARD_VIOLATION_URL)
        .name(
            "%s :: %s :: %s".formatted(REPO_NAME, VIOLATION_LOCATION, CODE_STANDARD_VIOLATION_NAME))
        .createdAt(CODE_STANDARD_VIOLATION_CREATED_AT);
  }
}
