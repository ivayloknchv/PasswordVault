package bg.sofia.uni.fmi.mjt.passwords.server.user.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WebsiteRegistrationTest {

  @Test
  void testOfMethodNullWebsite() {
    assertThrows(
        IllegalArgumentException.class, () -> WebsiteRegistration.of(null, "user", "pass"),
        "Should thrown an exception when website is null");
  }

  @Test
  void testOfMethodBlankWebsite() {
    assertThrows(
        IllegalArgumentException.class, () -> WebsiteRegistration.of("", "user", "pass"),
        "Should thrown an exception when website is blank");
  }

  @Test
  void testOfMethodNullUser() {
    assertThrows(
        IllegalArgumentException.class, () -> WebsiteRegistration.of("website", null, "pass"),
        "Should thrown an exception when user is null");
  }

  @Test
  void testOfMethodBlankUser() {
    assertThrows(
        IllegalArgumentException.class, () -> WebsiteRegistration.of("website", " ", "pass"),
        "Should thrown an exception when user is blank");
  }

  @Test
  void testOfMethodNullPassword() {
    assertThrows(
        IllegalArgumentException.class, () -> WebsiteRegistration.of("website", "user", null),
        "Should thrown an exception when password is null");
  }

  @Test
  void testOfMethodBlankPassword() {
    assertThrows(
        IllegalArgumentException.class, () -> WebsiteRegistration.of("website", "user", "  "),
        "Should thrown an exception when password is blank");
  }

  @Test
  void testOfMethodPasswordEncoding() {
    assertEquals(
        "YWJjZDEyMzQ=", WebsiteRegistration.of("website", "user", "abcd1234").password(),
        "Password should be correctly decoded");
  }

}