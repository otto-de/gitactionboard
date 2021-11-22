package de.otto.platform.gitactionboard;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.SneakyThrows;

public class TestUtil {

  @SneakyThrows
  public static String readFile(String fileName) {
    return Files.readString(Paths.get("./src/test/resources/" + fileName), UTF_8);
  }
}
