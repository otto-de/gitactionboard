package de.otto.platform.gitactionboard.adapters.controller;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLDecoder;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class Utils {

  public static ResponseEntity.BodyBuilder createResponseEntityBodyBuilder() {
    final HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setAccessControlAllowOrigin("*");

    return ResponseEntity.ok().headers(responseHeaders);
  }

  public static String decodeUrlEncodedText(String urlEncodedText) {
    return Optional.ofNullable(urlEncodedText)
        .map(value -> URLDecoder.decode(value, UTF_8))
        .orElse(null);
  }
}
