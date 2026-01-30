package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

import java.nio.channels.SelectionKey;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginCommandTest {
    private static SelectionKey selectionKeyMock;
    private static UserRepository userRepositoryMock;
    private static ClientSession clientSessionMock;
    private static Map<String, User> activeUsersMock;
    private static User userMock;

    private static Command command;
    private static String[] arguments;

    @BeforeAll
    static void setUp() {
        selectionKeyMock = mock(SelectionKey.class);
        userRepositoryMock = mock(UserRepository.class);
        clientSessionMock = mock(ClientSession.class);
        activeUsersMock = mock(Map.class);
        userMock = mock(User.class);

        command = new LoginCommand(userRepositoryMock, activeUsersMock);
        arguments = new String[] {"Jane Doe", "123456"};
    }

    @Test
    void testExecuteNullArgs() {
        assertEquals("Invalid arguments", command.execute(null, selectionKeyMock),
            "Command cannot accept null args");
    }

    @Test
    void testExecuteInvalidArgsCount() {
        assertEquals("login expected 2 arguments but found 4", command.execute(new String[4], selectionKeyMock),
            "Command expects different arguments count");
    }

    @Test
    void testExecuteUserLoggedInAnotherAccount() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);

        assertEquals("User is logged in another account", command.execute(arguments, selectionKeyMock),
            "User is logged in");
    }

    @Test
    void testExecuteUserLoggedIn() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);
        when(activeUsersMock.containsKey("Jane Doe")).thenReturn(true);

        assertEquals("User is logged in", command.execute(arguments, selectionKeyMock), "User is logged in");
    }

    @Test
    void testExecutionNonExistingUser() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);
        when(activeUsersMock.containsKey("Jane Doe")).thenReturn(false);
        when(userRepositoryMock.getUser(any())).thenReturn(null);

        assertEquals("User Jane Doe doesn't exist", command.execute(arguments, selectionKeyMock),
            "Should return a message for non-existing user");
    }

    @Test
    void testExecutionInvalidPassword() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);
        when(activeUsersMock.containsKey("Jane Doe")).thenReturn(false);
        when(userRepositoryMock.getUser(any())).thenReturn(userMock);
        when(userMock.isPasswordValid(any())).thenReturn(false);

        assertEquals("Incorrect password", command.execute(arguments, selectionKeyMock),
            "Should return a message for incorrect password");
    }

    @Test
    void testExecutionSuccess() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);
        when(activeUsersMock.containsKey("Jane Doe")).thenReturn(false);
        when(userRepositoryMock.getUser(any())).thenReturn(userMock);
        when(userMock.isPasswordValid(any())).thenReturn(true);

        assertEquals("Login successful", command.execute(arguments, selectionKeyMock),
            "Should return a message for login");
    }
}