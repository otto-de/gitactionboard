package de.otto.platform.gitactionboard.adapters.controller;

import static java.nio.charset.StandardCharsets.UTF_8;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@ConditionalOnExpression(
    "T(org.springframework.util.StringUtils).hasText('${BASIC_AUTH_USER_DETAILS_FILE_PATH:}') or T(org.springframework.util.StringUtils).hasText('${BASIC_AUTH_USER_DETAILS:}')")
public class BasicAuthenticationController {
  private static final String NAME = "name";
  private static final String USERNAME = "username";
  private static final String ACCESS_TOKEN = "access_token";

  private static final int ONE_DAY = 60 * 60 * 24;

  private final AuthenticationManager authenticationManager;
  private final String contextPath;

  @Autowired
  public BasicAuthenticationController(
      @Qualifier("basicAuthenticationManager") AuthenticationManager authenticationManager,
      @Qualifier("servletContextPath") String contextPath) {
    this.authenticationManager = authenticationManager;
    this.contextPath = contextPath;
  }

  @PostMapping(path = "/login/basic")
  public ResponseEntity<Object> login(
      @RequestBody AuthRequest authRequest, HttpServletResponse response) {
    final String username = authRequest.getUsername();
    final String password = authRequest.getPassword();
    try {
      final Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(username, password));

      if (authentication.isAuthenticated()) {
        response.addCookie(createCookie(NAME, username, ONE_DAY));
        response.addCookie(createCookie(USERNAME, username, ONE_DAY));
        response.addCookie(
            createCookie(ACCESS_TOKEN, createAccessToken(username, password), ONE_DAY));
        return ResponseEntity.ok().build();
      }
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  private String createAccessToken(String username, String password) {
    final byte[] encodedAuth =
        Base64.encodeBase64(String.format("%s:%s", username, password).getBytes(UTF_8), false);
    return String.format("Basic %s", new String(encodedAuth, UTF_8));
  }

  private Cookie createCookie(String cookieName, String value, int maxAge) {
    final Cookie cookie = new Cookie(cookieName, URLEncoder.encode(value, UTF_8));
    cookie.setMaxAge(maxAge);
    cookie.setPath(contextPath);
    return cookie;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class AuthRequest {
    private String username;
    private String password;
  }
}
