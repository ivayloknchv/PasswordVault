package bg.sofia.uni.fmi.mjt.passwords.server.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.HelpCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CommandFactoryTest {
    private static UserRepository userRepositoryMock;
    private static Map<String, User> activeUsers;

    @BeforeAll
    static void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        activeUsers = new HashMap<>();
    }

    @Test
    void testOfMethodInvalidCommandType() {
        assertNull(CommandFactory.of("delete", userRepositoryMock, activeUsers),
            "Should return null when an invalid command type is passed");
    }

    @Test
    void testOfMethodValidCommandType() {
        assertInstanceOf(HelpCommand.class, CommandFactory.of("help", userRepositoryMock, activeUsers),
            "Should return true when an valid command type is passed");
    }
}