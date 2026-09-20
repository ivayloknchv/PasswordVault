package bg.sofia.uni.fmi.mjt.passwords.server.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiKeysTest {

  private static final String API_KEYS = """
      {
        "key": "key",
        "secret": "secret"
      }
      """;

  private Path path;
  private Reader reader;
  private ApiKeys expected;

  @BeforeEach
  void setUp() throws IOException {
    path = Files.createTempFile("authenticationTest", ".txt");
    Files.writeString(path, API_KEYS);

    reader = new StringReader(API_KEYS);

    expected = new ApiKeys("key", "secret");
  }

  @Test
  void testOfNullPath() {
    assertThrows(
        IllegalArgumentException.class, () -> ApiKeys.of((Path) null),
        "Should thrown an exception when path is null");
  }

  @Test
  void testOfPath() {
    assertEquals(expected, ApiKeys.of(path), "ApiKeys not loaded as expected");
  }

  @Test
  void testOfNullReader() {
    assertThrows(
        IllegalArgumentException.class, () -> ApiKeys.of((Reader) null),
        "Should thrown an exception when reader is null");
  }

  @Test
  void testOfReader() {
    assertEquals(expected, ApiKeys.of(reader), "ApiKeys not loaded as expected");
  }
}