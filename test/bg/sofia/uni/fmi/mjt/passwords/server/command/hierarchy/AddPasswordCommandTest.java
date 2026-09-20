package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.PasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

@ExtendWith(MockitoExtension.class)
class AddPasswordCommandTest {

  private static final String[] ARGUMENTS = new String[] { "facebook", "Jane Doe", "123456" };

  @Mock
  private PasswordChecker passwordCheckerMock;

  @Mock
  private SelectionKey selectionKeyMock;

  @Mock
  private ClientSession clientSessionMock;

  @Mock
  private User userMock;

  @InjectMocks
  private AddPasswordCommand command;

  @Test
  void testExecuteNullArgs() {
    assertEquals("Invalid arguments", command.execute(null, selectionKeyMock), "Command cannot accept null args");
  }

  @Test
  void testExecuteInvalidArgsCount() {
    assertEquals(
        "add-password expected 3 arguments but found 4", command.execute(new String[4], selectionKeyMock),
        "Command expects different arguments count");
  }

  @Test
  void testExecuteUserNotLogged() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);

    assertEquals("User isn't logged in", command.execute(ARGUMENTS, selectionKeyMock), "No user is logged in");
  }

  @Test
  void testExecuteUserExistingAccount() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.websiteRegistrationExists(any(), any())).thenReturn(true);
    when(userMock.username()).thenReturn("currentUser");

    assertEquals(
        "User currentUser already has registration for facebook with username Jane Doe",
        command.execute(ARGUMENTS, selectionKeyMock), "Cannot add again an existing account");
  }

  @Test
  void testExecuteNotSecurePassword() throws CompromisedPasswordCheckException {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
    when(passwordCheckerMock.isValid(any())).thenReturn(false);

    assertEquals(
        "Password is not secure enough", command.execute(ARGUMENTS, selectionKeyMock),
        "Cannot accept compromised password");
  }

  @Test
  void testExecuteSuccess() throws CompromisedPasswordCheckException {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
    when(passwordCheckerMock.isValid(any())).thenReturn(true);

    assertEquals("Password added", command.execute(ARGUMENTS, selectionKeyMock), "Password expected to be added");
  }

  @Test
  void testExecuteServerError() throws CompromisedPasswordCheckException {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
    when(passwordCheckerMock.isValid(any())).thenThrow(new RuntimeException("error"));

    assertEquals(
        "Unexpected server error: error", command.execute(ARGUMENTS, selectionKeyMock),
        "Command cannot be executed when a server error happens");
  }

}