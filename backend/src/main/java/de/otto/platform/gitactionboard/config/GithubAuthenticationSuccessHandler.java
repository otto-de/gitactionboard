package de.otto.platform.gitactionboard.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
@Lazy
public class GithubAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final String LOGIN = "login";
  private static final String AVATAR_URL = "avatar_url";
  private static final String NAME = "name";
  private static final String USERNAME = "username";
  private static final int ONE_DAY = 60 * 60 * 24;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws ServletException, IOException {
    final DefaultOAuth2User authenticationPrincipal =
        (DefaultOAuth2User) authentication.getPrincipal();

    Stream.of(
            createCookie(authenticationPrincipal, USERNAME, LOGIN),
            createCookie(authenticationPrincipal, AVATAR_URL, AVATAR_URL),
            createCookie(authenticationPrincipal, NAME, NAME))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(response::addCookie);

    super.onAuthenticationSuccess(request, response, authentication);
  }

  private Optional<Cookie> createCookie(
      DefaultOAuth2User authenticationPrincipal, String cookieName, String attributeName) {
    return Optional.ofNullable(authenticationPrincipal.getAttribute(attributeName))
        .map(Object::toString)
        .map(value -> createCookie(cookieName, value));
  }

  private Cookie createCookie(String cookieName, String value) {
    final Cookie cookie = new Cookie(cookieName, URLEncoder.encode(value, UTF_8));
    cookie.setMaxAge(ONE_DAY);
    cookie.setPath("/");
    return cookie;
  }
}
