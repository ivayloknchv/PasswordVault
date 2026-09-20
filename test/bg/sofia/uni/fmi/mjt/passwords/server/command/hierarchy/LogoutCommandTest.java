package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

@ExtendWith(MockitoExtension.class)
class LogoutCommandTest {

  @Mock
  private SelectionKey selectionKeyMock;

  @Mock
  private ClientSession clientSessionMock;

  @Mock
  private User userMock;

  @Mock
  private Map<String, User> activeUsersMock;

  @InjectMocks
  private LogoutCommand command;

  @Test
  void testCreateCommandNullActiveUsers() {
    assertThrows(
        IllegalArgumentException.class, () -> new LogoutCommand(null),
        "Should thrown an exception when active users map is null");
  }

  @Test
  void testExecuteInvalidArgsCount() {
    assertEquals(
        "logout expected 0 arguments but found 4", command.execute(new String[4], selectionKeyMock),
        "Command expects different arguments count");
  }

  @Test
  void testExecuteNotLoggedIn() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);

    assertEquals(
        "User isn't logged in", command.execute(new String[0], selectionKeyMock),
        "Should return a message for not logged in user");
  }

  @Test
  void testExecuteSuccess() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);

    assertEquals(
        "Logout successful", command.execute(new String[0], selectionKeyMock),
        "Should return a message for a successful logout");
  }

}