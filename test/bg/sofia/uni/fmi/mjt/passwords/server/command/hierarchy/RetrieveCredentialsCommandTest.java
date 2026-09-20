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
class RetrieveCredentialsCommandTest {

  private static final String[] ARGUMENTS = new String[] { "facebook", "Jane Doe" };

  @Mock
  private SelectionKey selectionKeyMock;

  @Mock
  private ClientSession clientSessionMock;

  @Mock
  private User userMock;

  private final Command command = new RetrieveCredentialsCommand();

  @Test
  void testExecuteNullArgs() {
    assertEquals(
        "Invalid arguments", command.execute(null, selectionKeyMock),
        "Command cannot accept null args");
  }

  @Test
  void testExecuteInvalidArgsCount() {
    assertEquals(
        "retrieve-credentials expected 2 arguments but found 4",
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
  void testExecuteInvalidRegistration() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.retrieveCredentials(any(), any())).thenReturn(null);
    when(userMock.username()).thenReturn("currentUser");

    assertEquals(
        "User currentUser doesn't have registration for facebook with username Jane Doe",
        command.execute(ARGUMENTS, selectionKeyMock), "Command cannot retrieve non-existing credentials");
  }

  @Test
  void testExecuteSuccess() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
    when(userMock.retrieveCredentials(any(), any())).thenReturn("123456");

    assertEquals("123456", command.execute(ARGUMENTS, selectionKeyMock));
  }

}