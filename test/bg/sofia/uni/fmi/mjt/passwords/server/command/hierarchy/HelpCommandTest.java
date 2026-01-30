package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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

    Command command = new HelpCommand();
    SelectionKey selectionKey = mock(SelectionKey.class);

    @Test
    void testExecuteInvalidArgsCount() {
        assertEquals("help expected 0 arguments but found 1", command.execute(new String[1], selectionKey),
            "Command expects different arguments count");
    }

    @Test
    void testExecute() {
        assertEquals(EXPECTED_COMMANDS, command.execute(new String[0], selectionKey),
            "Returned command list doesn't match expected");
    }

}