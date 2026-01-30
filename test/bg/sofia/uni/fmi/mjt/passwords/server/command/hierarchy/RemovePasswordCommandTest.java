package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RemovePasswordCommandTest {
    private static SelectionKey selectionKeyMock;
    private static ClientSession clientSessionMock;
    private static User userMock;

    private static Command command;
    private static String[] arguments;

    @BeforeAll
    static void setUp() {
        selectionKeyMock = mock(SelectionKey.class);
        clientSessionMock = mock(ClientSession.class);
        userMock = mock(User.class);

        command = new RemovePasswordCommand();
        arguments = new String[] {"facebook", "Jane Doe"};
    }

    @Test
    void testExecuteNullArgs() {
        assertEquals("Invalid arguments", command.execute(null, selectionKeyMock),
            "Command cannot accept null args");
    }

    @Test
    void testExecuteInvalidArgsCount() {
        assertEquals("remove-password expected 2 arguments but found 4",
            command.execute(new String[4], selectionKeyMock),
            "Command expects different arguments count");
    }

    @Test
    void testExecuteUserNotLogged() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);

        assertEquals("User isn't logged in", command.execute(arguments, selectionKeyMock), "No user is logged in");
    }

    @Test
    void testExecuteUserNonExistingAccount() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
        when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
        when(userMock.username()).thenReturn("currentUser");

        assertEquals("User currentUser doesn't have registration for facebook with username Jane Doe",
            command.execute(arguments, selectionKeyMock), "Cannot remove a non-existing account");
    }

    @Test
    void testExecuteUserSuccess() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
        when(userMock.websiteRegistrationExists(any(), any())).thenReturn(true);
        when(userMock.username()).thenReturn("currentUser");

        assertEquals("Password removed", command.execute(arguments, selectionKeyMock), "Account should be removed");
    }

}