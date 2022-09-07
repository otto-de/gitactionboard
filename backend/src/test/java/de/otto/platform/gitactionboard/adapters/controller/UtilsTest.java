package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.adapters.controller.Utils.createResponseEntityBodyBuilder;
import static de.otto.platform.gitactionboard.adapters.controller.Utils.decodeUrlEncodedText;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.ResponseEntity;

@Parallel
class UtilsTest {
  @ParameterizedTest
  @CsvSource(value = {"Dummy text", "access token text!!"})
  void shouldDecodeUrlEncodedText(String text) {
    final String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

    assertThat(decodeUrlEncodedText(encodedText)).isEqualTo(text);
  }

  @Test
  void shouldReturnNullWhenUrlEncodeTextIsAlsoNull() {
    assertThat(decodeUrlEncodedText(null)).isNull();
  }

  @Test
  void shouldBuildResponseEntityBodyBuilderWithDefaultHeaders() {
    final ResponseEntity<Object> responseEntity = createResponseEntityBodyBuilder().build();
    assertThat(responseEntity.getHeaders()).isNotNull();
    assertThat(responseEntity.getHeaders().getAccessControlAllowOrigin()).isEqualTo("*");
  }
}
