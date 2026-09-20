package bg.sofia.uni.fmi.mjt.passwords.server.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.Command;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CommandExecutorTest {

  @Mock
  private SelectionKey selectionKeyMock;

  @Mock
  private Command commandMock;

  @Mock
  private CommandFactory commandFactoryMock;

  private CommandExecutor executor;

  @BeforeEach
  void setUp() {
    executor = new CommandExecutor(commandFactoryMock);
  }

  @Test
  void testCreateExecutorNullUserRepository() {
    assertThrows(
        IllegalArgumentException.class, () -> new CommandExecutor(null, Collections.emptyMap()),
        "Should thrown an exception when users repository is null");
  }

  @Test
  void testCreateExecutorNullActiveUsers() {
    assertThrows(
        IllegalArgumentException.class, () -> new CommandExecutor(mock(UserRepository.class), null),
        "Should thrown an exception when active users map is null");
  }

  @Test
  void testExecuteInvalidCommand() {
    assertEquals(
        "open is not a valid command", executor.execute("open", selectionKeyMock),
        "Should return a message for unsupported command");
  }

  @Test
  void testExecuteEmptyCommand() {
    assertEquals(
        "Command line is blank", executor.execute("", selectionKeyMock),
        "Should return a message for blank command");
  }

  @Test
  void testExecuteValidCommand() {
    when(commandFactoryMock.of(any())).thenReturn(commandMock);
    when(commandMock.execute(any(), any())).thenReturn("123456");

    assertEquals(
        "123456",
        executor.execute("retrieve-password                    website     username", selectionKeyMock),
        "Should return the password in plain text format");
  }
}