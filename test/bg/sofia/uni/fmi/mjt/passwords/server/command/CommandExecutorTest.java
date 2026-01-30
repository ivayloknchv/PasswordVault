package bg.sofia.uni.fmi.mjt.passwords.server.command;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandExecutorTest {
    private static SelectionKey selectionKeyMock;
    private static ClientSession clientSessionMock;
    private static User userMock;

    private static CommandExecutor executor;

    @BeforeAll
    static void setUp() {
        selectionKeyMock = mock(SelectionKey.class);
        clientSessionMock = mock(ClientSession.class);
        userMock = mock(User.class);

        executor = new CommandExecutor(mock(UserRepository.class), new HashMap<>());
    }

    @Test
    void testExecuteInvalidCommand() {
        assertEquals("open is not a valid command", executor.execute("open", selectionKeyMock),
            "Should return a message for unsupported command");
    }

    @Test
    void testExecuteEmptyCommand() {
        assertEquals("Command line is blank", executor.execute("", selectionKeyMock),
            "Should return a message for blank command");
    }

    @Test
    void testExecuteValidCommand() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
        when(userMock.retrieveCredentials(any(), any())).thenReturn("123456");

        assertEquals("123456",
            executor.execute("retrieve-credentials                    website     username", selectionKeyMock),
            "Should return the password in plain text format");
    }
}