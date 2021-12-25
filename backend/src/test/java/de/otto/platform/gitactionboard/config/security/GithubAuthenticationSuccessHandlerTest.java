package de.otto.platform.gitactionboard.config.security;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URLEncoder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@ExtendWith(MockitoExtension.class)
class GithubAuthenticationSuccessHandlerTest {
  private static final String EXPECTED_COOKIE_PATH = "/";
  @Mock private OAuth2AuthorizedClientService authorizedClientService;
  @Mock private OAuth2AuthorizedClient authorizedClient;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private OAuth2AuthenticationToken authentication;
  @Mock private DefaultOAuth2User defaultOAuth2User;
  @Mock private OAuth2AccessToken auth2AccessToken;
  @Mock private OAuth2RefreshToken auth2RefreshToken;

  private static final String LOGIN = "login";
  private static final String AVATAR_URL = "avatar_url";
  private static final String NAME = "name";
  private static final String USERNAME = "username";
  private static final String ACCESS_TOKEN = "access_token";
  private static final String REFRESH_TOKEN = "refresh_token";

  private static final int ONE_DAY = 60 * 60 * 24;
  private static final int SEVEN_HOURS = 60 * 60 * 7;
  private static final int FIVE_MONTHS = 60 * 60 * 5 * 30;

  private GithubAuthenticationSuccessHandler authenticationSuccessHandler;

  @BeforeEach
  void setUp() {
    final String clientRegistrationId = "client_registration_id";
    final String principleName = "principle_name";

    authenticationSuccessHandler = new GithubAuthenticationSuccessHandler(authorizedClientService);

    when(authentication.getPrincipal()).thenReturn(defaultOAuth2User);
    when(authentication.getAuthorizedClientRegistrationId()).thenReturn(clientRegistrationId);
    when(authentication.getName()).thenReturn(principleName);

    when(authorizedClientService.loadAuthorizedClient(clientRegistrationId, principleName))
        .thenReturn(authorizedClient);

    when(authorizedClient.getAccessToken()).thenReturn(auth2AccessToken);
    when(authorizedClient.getRefreshToken()).thenReturn(auth2RefreshToken);
  }

  @Test
  @SneakyThrows
  void shouldAddCookiesOnAuthenticationSuccess() {
    final String expectedLoginAttributeValue = "login_attribute";
    final String expectedNameAttributeValue = "login attribute";
    final String expectedAvatarUrlAttributeValue = "https://example.com";
    final String expectedAccessTokenAttributeValue = "secret_token";
    final String expectedRefreshTokenAttributeValue = "secret_refresh_token";

    when(defaultOAuth2User.getAttribute(LOGIN)).thenReturn(expectedLoginAttributeValue);
    when(defaultOAuth2User.getAttribute(AVATAR_URL)).thenReturn(expectedAvatarUrlAttributeValue);
    when(defaultOAuth2User.getAttribute(NAME)).thenReturn(expectedNameAttributeValue);

    when(auth2AccessToken.getTokenValue()).thenReturn(expectedAccessTokenAttributeValue);
    when(auth2RefreshToken.getTokenValue()).thenReturn(expectedRefreshTokenAttributeValue);

    authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

    final ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);

    verify(response, times(5)).addCookie(cookieArgumentCaptor.capture());

    assertThat(findCookie(cookieArgumentCaptor, USERNAME))
        .isNotNull()
        .satisfies(
            cookie -> {
              assertThat(cookie.getMaxAge()).isEqualTo(ONE_DAY);
              assertThat(cookie.getPath()).isEqualTo(EXPECTED_COOKIE_PATH);
              assertThat(cookie.getValue())
                  .isEqualTo(URLEncoder.encode(expectedLoginAttributeValue, UTF_8));
            });

    assertThat(findCookie(cookieArgumentCaptor, AVATAR_URL))
        .isNotNull()
        .satisfies(
            cookie -> {
              assertThat(cookie.getMaxAge()).isEqualTo(ONE_DAY);
              assertThat(cookie.getPath()).isEqualTo(EXPECTED_COOKIE_PATH);
              assertThat(cookie.getValue())
                  .isEqualTo(URLEncoder.encode(expectedAvatarUrlAttributeValue, UTF_8));
            });

    assertThat(findCookie(cookieArgumentCaptor, NAME))
        .isNotNull()
        .satisfies(
            cookie -> {
              assertThat(cookie.getMaxAge()).isEqualTo(ONE_DAY);
              assertThat(cookie.getPath()).isEqualTo(EXPECTED_COOKIE_PATH);
              assertThat(cookie.getValue())
                  .isEqualTo(URLEncoder.encode(expectedNameAttributeValue, UTF_8));
            });

    assertThat(findCookie(cookieArgumentCaptor, ACCESS_TOKEN))
        .isNotNull()
        .satisfies(
            cookie -> {
              assertThat(cookie.getMaxAge()).isEqualTo(SEVEN_HOURS);
              assertThat(cookie.getPath()).isEqualTo(EXPECTED_COOKIE_PATH);
              assertThat(cookie.getValue())
                  .isEqualTo(
                      URLEncoder.encode(
                          String.format("token %s", expectedAccessTokenAttributeValue), UTF_8));
            });

    assertThat(findCookie(cookieArgumentCaptor, REFRESH_TOKEN))
        .isNotNull()
        .satisfies(
            cookie -> {
              assertThat(cookie.getMaxAge()).isEqualTo(FIVE_MONTHS);
              assertThat(cookie.getPath()).isEqualTo(EXPECTED_COOKIE_PATH);
              assertThat(cookie.getValue())
                  .isEqualTo(URLEncoder.encode(expectedRefreshTokenAttributeValue, UTF_8));
            });
  }

  private Cookie findCookie(ArgumentCaptor<Cookie> cookieArgumentCaptor, String cookieName) {
    return cookieArgumentCaptor.getAllValues().stream()
        .filter(cookie -> cookieName.equals(cookie.getName()))
        .findFirst()
        .orElse(null);
  }
}
