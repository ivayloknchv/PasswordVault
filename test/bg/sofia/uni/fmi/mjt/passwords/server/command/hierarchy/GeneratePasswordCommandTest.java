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
import bg.sofia.uni.fmi.mjt.passwords.server.exception.PasswordGenerateException;
import bg.sofia.uni.fmi.mjt.passwords.server.generator.PasswordGenerator;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

@ExtendWith(MockitoExtension.class)
class GeneratePasswordCommandTest {

  private static final String[] ARGUMENTS = new String[] { "facebook", "Jane Doe" };

  @Mock
  private PasswordGenerator passwordGeneratorMock;

  @Mock
  private SelectionKey selectionKeyMock;

  @Mock
  private ClientSession clientSessionMock;

  @Mock
  private User userMock;

  @InjectMocks
  private GeneratePasswordCommand command;

  @Test
  void testExecuteNullArgs() {
    assertEquals(
        "Invalid arguments", command.execute(null, selectionKeyMock),
        "Command cannot accept null args");
  }

  @Test
  void testExecuteInvalidArgsCount() {
    assertEquals(
        "generate-password expected 2 arguments but found 4",
        command.execute(new String[4], selectionKeyMock),
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
  void testExecuteSuccess() throws PasswordGenerateException {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
    when(passwordGeneratorMock.generatePassword()).thenReturn("newPassword");

    assertEquals(
        "newPassword", command.execute(ARGUMENTS, selectionKeyMock),
        "Command should return the generated password");
  }

  @Test
  void testExecuteServerError() throws PasswordGenerateException {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
    when(passwordGeneratorMock.generatePassword()).thenThrow(new PasswordGenerateException("error"));

    assertEquals(
        "Unexpected server error: error", command.execute(ARGUMENTS, selectionKeyMock),
        "Command cannot be executed when a server error happens");
  }
}