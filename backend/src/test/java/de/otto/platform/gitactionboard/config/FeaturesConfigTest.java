package de.otto.platform.gitactionboard.config;

import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Parallel
class FeaturesConfigTest {
  private static final String FALSE_TEXT_VALUE = "false";
  private FeaturesConfig featuresConfig;

  public static Stream<Arguments> getArguments() {
    return Stream.of(
        Arguments.of(FALSE_TEXT_VALUE, false),
        Arguments.of("true", true),
        Arguments.of("", false),
        Arguments.of("null", false));
  }

  @BeforeEach
  void setUp() {
    featuresConfig = new FeaturesConfig();
  }

  @ParameterizedTest
  @MethodSource("getArguments")
  void shouldGiveValidConfigForSecretsScan(
      String secretsScanEnabled, boolean expectedSecretsScanEnabled) {

    assertThat(featuresConfig.enableSecretsScanMonitoring(secretsScanEnabled))
        .isEqualTo(expectedSecretsScanEnabled);
  }

  @ParameterizedTest
  @MethodSource("getArguments")
  void shouldGiveValidConfigForCodeScan(String codeScanEnabled, boolean expectedCodeScanEnabled) {

    assertThat(featuresConfig.enableCodeScanMonitoring(codeScanEnabled))
        .isEqualTo(expectedCodeScanEnabled);
  }
}
