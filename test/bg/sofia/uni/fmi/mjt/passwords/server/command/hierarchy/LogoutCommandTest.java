package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

import java.nio.channels.SelectionKey;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogoutCommandTest {
    private static SelectionKey selectionKeyMock;
    private static ClientSession clientSessionMock;
    private static User userMock;

    private static Command command;

    @BeforeAll
    static void setUp() {
        selectionKeyMock = mock(SelectionKey.class);
        clientSessionMock = mock(ClientSession.class);
        userMock = mock(User.class);

        command = new LogoutCommand(mock(Map.class));
    }

    @Test
    void testExecuteInvalidArgsCount() {
        assertEquals("logout expected 0 arguments but found 4", command.execute(new String[4], selectionKeyMock),
            "Command expects different arguments count");
    }

    @Test
    void testExecuteNotLoggedIn() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);

        assertEquals("User isn't logged in", command.execute(new String[0], selectionKeyMock),
            "Should return a message for not logged in user");
    }

    @Test
    void testExecuteSuccess() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);

        assertEquals("Logout successful", command.execute(new String[0], selectionKeyMock),
            "Should return a message for a successful logout");
    }

}