package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.passwords.server.generator.PasswordGenerator;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.PasswordGenerateException;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeneratePasswordCommandTest {
    private static PasswordGenerator passwordGeneratorMock;
    private static SelectionKey selectionKeyMock;
    private static ClientSession clientSessionMock;
    private static User userMock;

    private static Command command;
    private static String[] arguments;

    @BeforeAll
    static void setUp() {
        passwordGeneratorMock = mock(PasswordGenerator.class);
        selectionKeyMock = mock(SelectionKey.class);
        clientSessionMock = mock(ClientSession.class);
        userMock = mock(User.class);

        command = new GeneratePasswordCommand(passwordGeneratorMock);
        arguments = new String[] {"facebook", "Jane Doe"};
    }

    @Test
    void testExecuteNullArgs() {
        assertEquals("Invalid arguments", command.execute(null, selectionKeyMock),
            "Command cannot accept null args");
    }

    @Test
    void testExecuteInvalidArgsCount() {
        assertEquals("generate-password expected 2 arguments but found 4",
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
    void testExecuteUserExistingAccount() {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
        when(userMock.websiteRegistrationExists(any(), any())).thenReturn(true);
        when(userMock.username()).thenReturn("currentUser");

        assertEquals("User currentUser already has registration for facebook with username Jane Doe",
            command.execute(arguments, selectionKeyMock), "Cannot add again an existing account");
    }

    @Test
    void testExecuteSuccess() throws PasswordGenerateException {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
        when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
        when(passwordGeneratorMock.generatePassword()).thenReturn("newPassword");

        assertEquals("newPassword", command.execute(arguments, selectionKeyMock),
            "Command should return the generated password");
    }

    @Test
    void testExecuteServerError() throws PasswordGenerateException {
        when(selectionKeyMock.attachment()).thenReturn(clientSessionMock);
        when(clientSessionMock.getLoggedUser()).thenReturn(userMock);
        when(userMock.websiteRegistrationExists(any(), any())).thenReturn(false);
        when(passwordGeneratorMock.generatePassword()).thenThrow(new PasswordGenerateException("error"));

        assertEquals("Unexpected server error", command.execute(arguments, selectionKeyMock),
            "Command cannot be executed when a server error happens");
    }
}