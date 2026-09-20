package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginCommandTest {

  private static final String[] ARGUMENTS = new String[] { "Jane Doe", "123456" };

  @Mock
  private SelectionKey selectionKeyMock;

  @Mock
  private UserRepository userRepositoryMock;

  @Mock
  private ClientSession clientSessionMock;

  @Mock
  private Map<String, User> activeUsersMock;

  @Mock
  private User userMock;

  @InjectMocks
  private LoginCommand command;

  @Test
  void testCreateCommandNullUsersRepo() {
    assertThrows(
        IllegalArgumentException.class, () -> new LoginCommand(null, Collections.emptyMap()),
        "Should thrown an exception when users repo is null");
  }

  @Test
  void testCreateCommandNullActiveUsers() {
    assertThrows(
        IllegalArgumentException.class, () -> new LoginCommand(userRepositoryMock, null),
        "Should thrown an exception when active users map is null");
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
        "login expected 2 arguments but found 4", command.execute(new String[4], selectionKeyMock),
        "Command expects different arguments count");
  }

  @Test
  void testExecuteUserLoggedInAnotherAccount() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(userMock);

    assertEquals(
        "User is logged in another account", command.execute(ARGUMENTS, selectionKeyMock),
        "User is logged in");
  }

  @Test
  void testExecuteUserLoggedIn() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(activeUsersMock.containsKey("Jane Doe")).thenReturn(true);

    assertEquals("User is logged in", command.execute(ARGUMENTS, selectionKeyMock), "User is logged in");
  }

  @Test
  void testExecutionNonExistingUser() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(activeUsersMock.containsKey("Jane Doe")).thenReturn(false);
    when(userRepositoryMock.getUser(any())).thenReturn(null);

    assertEquals(
        "User Jane Doe doesn't exist", command.execute(ARGUMENTS, selectionKeyMock),
        "Should return a message for non-existing user");
  }

  @Test
  void testExecutionInvalidPassword() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(activeUsersMock.containsKey("Jane Doe")).thenReturn(false);
    when(userRepositoryMock.getUser(any())).thenReturn(userMock);
    when(userMock.isPasswordValid(any())).thenReturn(false);

    assertEquals(
        "Incorrect password", command.execute(ARGUMENTS, selectionKeyMock),
        "Should return a message for incorrect password");
  }

  @Test
  void testExecutionSuccess() {
    when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
    when(clientSessionMock.getLoggedUser()).thenReturn(null);
    when(activeUsersMock.containsKey("Jane Doe")).thenReturn(false);
    when(userRepositoryMock.getUser(any())).thenReturn(userMock);
    when(userMock.isPasswordValid(any())).thenReturn(true);

    assertEquals(
        "Login successful", command.execute(ARGUMENTS, selectionKeyMock),
        "Should return a message for login");
  }
}