package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.CompromisedPasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RegisterCommandTest {

  private static final String[] ARGUMENTS_1 = new String[] { "user", "pass", "pass" };

  private static final String[] arguments2 = new String[] { "user", "pass", "Pass" };

  @Mock
  private SelectionKey selectionKeyMock;

  @Mock
  private UserRepository userRepositoryMock;

  @Mock
  private CompromisedPasswordChecker passwordCheckerMock;

  @Mock
  private ClientSession clientSessionMock;

  @Mock
  private User userMock;

  @InjectMocks
  private RegisterCommand command;

  @Test
  void testCreateCommandNullUsersRepo() {
    assertThrows(
        IllegalArgumentException.class, () -> new RegisterCommand(null),
        "Should thrown an exception when users repo is null");
  }

  @Test
  void testExecuteNullArgs() {
    assertEquals(
        "Invalid arguments", command.execute(null, selectionKeyMock),
        "Command cannot accept null args");
  }

  @Test
  void testExecuteInvalidArgsCount() {
    assertEquals(
        "register expected 3 arguments but found 4", command.execute(new String[4], selectionKeyMock),
        "Command expects different arguments count");
  }

  @Test
  void testExecuteAlreadyLoggedIn() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);

    assertEquals("User is logged in", command.execute(ARGUMENTS_1, selectionKeyMock), "User is logged in");
  }

  @Test
  void testExecuteUserExists() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(userRepositoryMock.getUser(any())).thenReturn(userMock);

    assertEquals(
        "User user already exists", command.execute(ARGUMENTS_1, selectionKeyMock),
        "Should return a message for existing user");
  }

  @Test
  void testExecuteNonMatchingPassword() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(userRepositoryMock.getUser(any())).thenReturn(null);

    assertEquals(
        "Passwords don't match", command.execute(arguments2, selectionKeyMock),
        "Should return a message for non-matching passwords");
  }

  @Test
  void testExecuteWeakPassword() throws CompromisedPasswordCheckException {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(userRepositoryMock.getUser(any())).thenReturn(null);
    when(passwordCheckerMock.isValid(any())).thenReturn(false);

    assertEquals(
        "Password is not secure enough", command.execute(ARGUMENTS_1, selectionKeyMock),
        "Should return a message for weak password");
  }

  @Test
  void testExecuteWeakPasswordCheckFails() throws CompromisedPasswordCheckException {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(userRepositoryMock.getUser(any())).thenReturn(null);
    when(passwordCheckerMock.isValid(any())).thenThrow(new CompromisedPasswordCheckException("Error"));

    assertEquals(
        "Unexpected server error: Error", command.execute(ARGUMENTS_1, selectionKeyMock),
        "Should return a message for internal server error");
  }

  @Test
  void testExecuteSuccess() throws CompromisedPasswordCheckException {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(userRepositoryMock.getUser(any())).thenReturn(null);
    when(passwordCheckerMock.isValid(any())).thenReturn(true);

    assertEquals(
        "User user has been registered", command.execute(ARGUMENTS_1, selectionKeyMock),
        "Should return a message registered user");
  }
}