package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.channels.SelectionKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HelpCommandTest {

  private static final String EXPECTED_COMMANDS = """
      register <user> <password> <password-repeat>
      login <user> <password>
      logout
      retrieve-credentials <website> <user>
      generate-password <website> <user>
      add-password <website> <user> <password>
      remove-password <website> <user>
      disconnect
      help""";

  @Mock
  private SelectionKey selectionKeyMock;

  private final Command command = new HelpCommand();

  @Test
  void testExecuteInvalidArgsCount() {
    assertEquals(
        "help expected 0 arguments but found 1", command.execute(new String[1], selectionKeyMock),
        "Command expects different arguments count");
  }

  @Test
  void testExecute() {
    assertEquals(
        EXPECTED_COMMANDS, command.execute(new String[0], selectionKeyMock),
        "Returned command list doesn't match expected");
  }

}