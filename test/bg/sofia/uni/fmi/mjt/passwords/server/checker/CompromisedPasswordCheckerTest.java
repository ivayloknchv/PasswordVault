package bg.sofia.uni.fmi.mjt.passwords.server.checker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.checker.client.EnzoicHttpClient;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;
import bg.sofia.uni.fmi.mjt.passwords.server.log.Logger;

@ExtendWith(MockitoExtension.class)
class CompromisedPasswordCheckerTest {

  private static final String API_RESPONSE = """
      {
         "candidates":[
            {
               "sha256":"a3f9c2d8e1gvdhj021300320021ejndbnwbej7328382631bhdsbds123823323d"
            },
            {
               "sha256":"932b004070f4e21226b59f039b437cd2ffa7d31da0baef37cab15c7732ece352"
            }
         ]
      }
      """;

  @Mock
  private HttpResponse<String> responseMock;

  @Mock
  private Logger loggerMock;

  @InjectMocks
  private CompromisedPasswordChecker passwordChecker;

  @Test
  void testIsValidNullPassword() {
    assertThrows(
        IllegalArgumentException.class, () -> passwordChecker.isValid(null),
        "Should thrown an exception when password is null");
  }

  @Test
  void testIsValidBlankPassword() {
    assertThrows(
        IllegalArgumentException.class, () -> passwordChecker.isValid(""),
        "Should thrown an exception when password is blank");
  }

  @Test
  void testIsValidTrueNotFound() throws CompromisedPasswordCheckException {
    try (var _ = mockConstruction(
        EnzoicHttpClient.class,
        (mock, context) -> when(mock.fetchPasswordsHashes(anyString())).thenReturn(responseMock))) {
      when(responseMock.statusCode()).thenReturn(404);
      assertTrue(passwordChecker.isValid("dummyPassword"));
    }
  }

  @Test
  void testIsValidUnexpectedError() {
    try (var _ = mockConstruction(
        EnzoicHttpClient.class,
        (mock, context) -> when(mock.fetchPasswordsHashes(anyString())).thenReturn(responseMock))) {
      when(responseMock.statusCode()).thenReturn(500);
      assertThrows(CompromisedPasswordCheckException.class, () -> passwordChecker.isValid("password"), "Unexpected error occurred while fetching");
    }
  }

  @Test
  void testIsValidFalse() throws CompromisedPasswordCheckException {
    try (var _ = mockConstruction(
        EnzoicHttpClient.class,
        (mock, context) -> when(mock.fetchPasswordsHashes(anyString())).thenReturn(responseMock))) {
      when(responseMock.statusCode()).thenReturn(200);
      when(responseMock.body()).thenReturn(API_RESPONSE);

      assertFalse(passwordChecker.isValid("dummyPassword"), "Password is in the list of compromised password");
    }
  }

  @Test
  void testIsValidTrueNotFoundInList() throws CompromisedPasswordCheckException {
    try (var _ = mockConstruction(
        EnzoicHttpClient.class,
        (mock, context) -> when(mock.fetchPasswordsHashes(anyString())).thenReturn(responseMock))) {
      when(responseMock.statusCode()).thenReturn(200);
      when(responseMock.body()).thenReturn(API_RESPONSE);

      assertTrue(passwordChecker.isValid("dummyPass"), "Password is in the list of compromised password");
    }
  }
}
