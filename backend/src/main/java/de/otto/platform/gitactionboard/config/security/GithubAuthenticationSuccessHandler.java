package de.otto.platform.gitactionboard.config.security;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
@Lazy
@Slf4j
public class GithubAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final String LOGIN = "login";
  private static final String AVATAR_URL = "avatar_url";
  private static final String NAME = "name";
  private static final String USERNAME = "username";
  private static final String ACCESS_TOKEN = "access_token";
  private static final String REFRESH_TOKEN = "refresh_token";

  private static final int ONE_DAY = 60 * 60 * 24;
  private static final int SEVEN_HOURS = 60 * 60 * 7;
  private static final int FIVE_MONTHS = 60 * 60 * 5 * 30;

  private final OAuth2AuthorizedClientService clientService;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws ServletException, IOException {
    final DefaultOAuth2User authenticationPrincipal =
        (DefaultOAuth2User) authentication.getPrincipal();

    final Optional<OAuth2AuthorizedClient> optionalAuthorisedClient =
        Optional.ofNullable(getAuthorisedClient(authentication));

    final Optional<OAuth2AccessToken> optionalAccessToken =
        optionalAuthorisedClient.map(OAuth2AuthorizedClient::getAccessToken);
    final Optional<OAuth2RefreshToken> optionalRefreshToken =
        optionalAuthorisedClient.map(OAuth2AuthorizedClient::getRefreshToken);

    Stream.of(
            createCookie(authenticationPrincipal, USERNAME, LOGIN),
            createCookie(authenticationPrincipal, AVATAR_URL, AVATAR_URL),
            createCookie(authenticationPrincipal, NAME, NAME),
            optionalAccessToken.map(this::createAccessTokenCookie),
            optionalRefreshToken.map(this::createRefreshTokenCookie))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(response::addCookie);

    response.sendRedirect("/#/dashboard");
  }

  private Cookie createAccessTokenCookie(OAuth2AccessToken oAuth2AccessToken) {
    return createCookie(
        ACCESS_TOKEN, String.format("token %s", oAuth2AccessToken.getTokenValue()), SEVEN_HOURS);
  }

  private Cookie createRefreshTokenCookie(OAuth2RefreshToken oAuth2RefreshToken) {
    return createCookie(REFRESH_TOKEN, oAuth2RefreshToken.getTokenValue(), FIVE_MONTHS);
  }

  private OAuth2AuthorizedClient getAuthorisedClient(Authentication authentication) {
    if (OAuth2AuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
      final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
      final String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
      return clientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName());
    }

    return null;
  }

  private Optional<Cookie> createCookie(
      DefaultOAuth2User authenticationPrincipal, String cookieName, String attributeName) {
    return Optional.ofNullable(authenticationPrincipal.getAttribute(attributeName))
        .map(Object::toString)
        .map(value -> createCookie(cookieName, value, ONE_DAY));
  }

  private Cookie createCookie(String cookieName, String value, int maxAge) {
    final Cookie cookie = new Cookie(cookieName, URLEncoder.encode(value, UTF_8));
    cookie.setMaxAge(maxAge);
    cookie.setPath("/");
    return cookie;
  }
}
