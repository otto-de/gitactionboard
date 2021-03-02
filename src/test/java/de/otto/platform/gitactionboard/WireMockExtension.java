package de.otto.platform.gitactionboard;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class WireMockExtension implements BeforeAllCallback, AfterAllCallback, AfterEachCallback {

  private WireMockServer server;

  @Override
  public void beforeAll(ExtensionContext context) {
    server = new WireMockServer(wireMockConfig().port(65035));
    server.start();
    WireMock.configureFor("localhost", server.port());
    log.info("====> WireMock server started at PORT {}", server.port());
    log.info(
        "====> WireMock admin page can be access here: http://localhost:{}/__admin/",
        server.port());
  }

  @Override
  public void afterEach(ExtensionContext context) {
    log.info("====> WireMock server resetting all stubs");
    server.resetAll();
  }

  @Override
  public void afterAll(ExtensionContext context) {
    server.stop();
    log.info("====> WireMock server stopped");
  }
}
