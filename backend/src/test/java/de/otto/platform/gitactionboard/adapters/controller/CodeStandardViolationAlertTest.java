package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.getCodeStandardViolationAlertBuilder;
import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.getCodeStandardViolationDetailsBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import org.junit.jupiter.api.Test;

class CodeStandardViolationAlertTest {
  @Test
  void shouldConvertFromCodeStandardViolationDetails() {
    final CodeStandardViolationDetails violationDetails =
        getCodeStandardViolationDetailsBuilder().build();
    final CodeStandardViolationAlert violationAlert =
        CodeStandardViolationAlert.from(violationDetails);

    assertThat(violationAlert).isEqualTo(getCodeStandardViolationAlertBuilder().build());
  }
}
