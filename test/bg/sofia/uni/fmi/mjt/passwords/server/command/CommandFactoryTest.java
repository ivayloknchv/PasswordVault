package bg.sofia.uni.fmi.mjt.passwords.server.command;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.AddPasswordCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.GeneratePasswordCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.HelpCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.LoginCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.LogoutCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.RegisterCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.RemovePasswordCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.RetrieveCredentialsCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CommandFactoryTest {

  @Mock
  private UserRepository userRepositoryMock;

  @Mock
  private Map<String, User> activeUsersMock;

  @InjectMocks
  private CommandFactory commandFactory;

  @Test
  void testCreateFactoryNullUserRepository() {
    assertThrows(
        IllegalArgumentException.class, () -> new CommandExecutor(null, activeUsersMock),
        "Should thrown an exception when users repository is null");
  }

  @Test
  void testCreateFactoryNullActiveUsers() {
    assertThrows(
        IllegalArgumentException.class, () -> new CommandExecutor(userRepositoryMock, null),
        "Should thrown an exception when active users map is null");
  }

  @Test
  void testOfMethodRegisterCommand() {
    assertInstanceOf(
        RegisterCommand.class, commandFactory.of("register"));
  }

  @Test
  void testOfMethodLoginCommand() {
    assertInstanceOf(
        LoginCommand.class, commandFactory.of("login"));
  }

  @Test
  void testOfMethodLogoutCommand() {
    assertInstanceOf(
        LogoutCommand.class, commandFactory.of("logout"));
  }

  @Test
  void testOfMethodRetrieveCredentialsCommand() {
    assertInstanceOf(
        RetrieveCredentialsCommand.class, commandFactory.of("retrieve-credentials"));
  }

  @Test
  void testOfMethodGeneratePasswordCommand() {
    assertInstanceOf(
        GeneratePasswordCommand.class, commandFactory.of("generate-password"));
  }

  @Test
  void testOfMethodAddPasswordCommand() {
    assertInstanceOf(
        AddPasswordCommand.class, commandFactory.of("add-password"));
  }

  @Test
  void testOfMethodRemovePasswordCommand() {
    assertInstanceOf(
        RemovePasswordCommand.class, commandFactory.of("remove-password"));
  }

  @Test
  void testOfMethodDisconnectCommand() {
    assertInstanceOf(
        DisconnectCommand.class, commandFactory.of("disconnect"));
  }

  @Test
  void testOfMethodHelpCommand() {
    assertInstanceOf(
        HelpCommand.class, commandFactory.of("help"));
  }

  @Test
  void testOfMethodInvalidCommandType() {
    assertNull(
        commandFactory.of("delete"));
  }
}