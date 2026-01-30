package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegisterCommandTest {
    private static SelectionKey selectionKeyMock;
    private static UserRepository userRepositoryMock;
    private static ClientSession clientSessionMock;
    private static User userMock;

    private static Command command;
    private static String[] arguments1;
    private static String[] arguments2;

    @BeforeAll
    static void setUp() {
        selectionKeyMock = mock(SelectionKey.class);
        userRepositoryMock = mock(UserRepository.class);
        clientSessionMock = mock(ClientSession.class);
        userMock = mock(User.class);

        command = new RegisterCommand(userRepositoryMock);
        arguments1 = new String[] {"user", "pass", "pass"};
        arguments2 = new String[] {"user", "pass", "Pass"};
    }

    @Test
    void testExecuteNullArgs() {
        assertEquals("Invalid arguments", command.execute(null, selectionKeyMock),
            "Command cannot accept null args");
    }

    @Test
    void testExecuteInvalidArgsCount() {
        assertEquals("register expected 3 arguments but found 4", command.execute(new String[4], selectionKeyMock),
            "Command expects different arguments count");
    }

    @Test
    void testExecuteAlreadyLoggedIn() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);

        assertEquals("User is logged in", command.execute(arguments1, selectionKeyMock), "User is logged in");
    }

    @Test
    void testExecuteUserExists() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);
        when(userRepositoryMock.getUser(any())).thenReturn(userMock);

        assertEquals("User user already exists", command.execute(arguments1, selectionKeyMock),
            "Should return a message for existing user");
    }

    @Test
    void testExecuteNonMatchingPassword() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);
        when(userRepositoryMock.getUser(any())).thenReturn(null);

        assertEquals("Passwords don't match", command.execute(arguments2, selectionKeyMock),
            "Should return a message for non-matching passwords");
    }

    @Test
    void testExecuteSuccess() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(null);
        when(userRepositoryMock.getUser(any())).thenReturn(null);

        assertEquals("User user has been registered", command.execute(arguments1, selectionKeyMock),
            "Should return a message registered user");
    }
}