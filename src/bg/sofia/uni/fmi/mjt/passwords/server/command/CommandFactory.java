package bg.sofia.uni.fmi.mjt.passwords.server.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.AddPasswordCommand;
import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.Command;
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
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

public class CommandFactory {

  private final UserRepository userRepository;
  private final Map<String, User> activeUsers;

  public CommandFactory(UserRepository userRepository, Map<String, User> activeUsers) {
    Validator.validateNullObject(Field.USER_REPOSITORY, userRepository);
    Validator.validateNullObject(Field.ACTIVE_USERS, activeUsers);

    this.userRepository = userRepository;
    this.activeUsers = activeUsers;
  }

  public Command of(String commandType) {
    CommandType command = CommandType.getFromString(commandType);
    if (command == null) {
      return null;
    }

    return switch (command) {
      case REGISTER -> new RegisterCommand(userRepository);
      case LOGIN -> new LoginCommand(userRepository, activeUsers);
      case LOGOUT -> new LogoutCommand(activeUsers);
      case RETRIEVE_CREDENTIALS -> new RetrieveCredentialsCommand();
      case GENERATE_PASSWORD -> new GeneratePasswordCommand();
      case ADD_PASSWORD -> new AddPasswordCommand();
      case REMOVE_PASSWORD -> new RemovePasswordCommand();
      case DISCONNECT -> new DisconnectCommand(activeUsers);
      case HELP -> new HelpCommand();
    };
  }
}
