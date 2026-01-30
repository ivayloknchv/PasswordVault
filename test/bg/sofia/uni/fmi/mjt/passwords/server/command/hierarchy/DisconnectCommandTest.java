package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.Command;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.DisconnectCommand;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DisconnectCommandTest {
    private static SelectionKey selectionKeyMock;
    private static ClientSession clientSessionMock;
    private static Command command;

    @BeforeAll
    static void setUp() {
        selectionKeyMock = mock(SelectionKey.class);
        clientSessionMock = mock(ClientSession.class);
        command = new DisconnectCommand(new HashMap<>());
    }

    @Test
    void testExecuteInvalidArgsCount() {
        assertEquals("disconnect expected 0 arguments but found 4", command.execute(new String[4], selectionKeyMock),
            "Command expects different arguments count");
    }

    @Test
    void testExecuteSuccess() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);

        assertEquals("Disconnected", command.execute(new String[0], selectionKeyMock),
            "User is disconnected from the server");
    }

}