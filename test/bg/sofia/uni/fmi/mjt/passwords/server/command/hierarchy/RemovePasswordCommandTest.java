package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

@ExtendWith(MockitoExtension.class)
class RemovePasswordCommandTest {

  private static final String[] ARGUMENTS = new String[] { "facebook", "Jane Doe" };

  @Mock
  private SelectionKey selectionKeyMock;

  @Mock
  private ClientSession clientSessionMock;

  @Mock
  private User userMock;

  private final RemovePasswordCommand command = new RemovePasswordCommand();

  @Test
  void testExecuteNullArgs() {
    assertEquals(
        "Invalid arguments", command.execute(null, selectionKeyMock),
        "Command cannot accept null args");
  }

  @Test
  void testExecuteInvalidArgsCount() {
    assertEquals(
        "remove-password expected 2 arguments but found 4",
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
  void testExecuteUserNonExistingAccount() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
    when(userMock.username()).thenReturn("currentUser");

    assertEquals(
        "User currentUser doesn't have registration for facebook with username Jane Doe",
        command.execute(ARGUMENTS, selectionKeyMock), "Cannot remove a non-existing account");
  }

  @Test
  void testExecuteUserSuccess() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.websiteRegistrationExists(any(), any())).thenReturn(true);

    assertEquals("Password removed", command.execute(ARGUMENTS, selectionKeyMock), "Account should be removed");
  }

}