package bg.sofia.uni.fmi.mjt.passwords.server.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.checker.PasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.PasswordGenerateException;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorImplTest {

  private static final int EXPECTED_PASSWORD_LENGTH = 20;

  @Mock
  private PasswordChecker passwordCheckerMock;

  @InjectMocks
  private PasswordGeneratorImpl passwordGenerator;

  @Test
  void testGeneratePasswordSuccess() throws PasswordGenerateException, CompromisedPasswordCheckException {
    when(passwordCheckerMock.isValid(any())).thenReturn(true);

    String generatedPassword = passwordGenerator.generatePassword();

    assertEquals(
        EXPECTED_PASSWORD_LENGTH, generatedPassword.length(),
        "Generated password length is different form 20");
    assertTrue(
        generatedPassword.chars().anyMatch(ch -> ch >= 'a' && ch <= 'z'),
        "Generated password doesn't contain at least one lowercase letter");
    assertTrue(
        generatedPassword.chars().anyMatch(ch -> ch >= 'A' && ch <= 'Z'),
        "Generated password doesn't contain at least one uppercase letter");
    assertTrue(
        generatedPassword.chars().anyMatch(ch -> ch >= '0' && ch <= '9'),
        "Generated password doesn't contain at least one digit");
    assertTrue(
        generatedPassword.chars().anyMatch(ch -> ch >= '!' && ch <= '/'),
        "Generated password doesn't contain at least one special symbol");
  }

  @Test
  void testGeneratePasswordFailure() throws CompromisedPasswordCheckException {
    when(passwordCheckerMock.isValid(any())).thenThrow(new CompromisedPasswordCheckException("error"));

    assertThrows(
        PasswordGenerateException.class, () -> passwordGenerator.generatePassword(),
        "Internal error happened");
  }
}