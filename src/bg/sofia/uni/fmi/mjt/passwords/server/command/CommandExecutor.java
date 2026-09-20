package bg.sofia.uni.fmi.mjt.passwords.server.command;

import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.Command;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

public class CommandExecutor {
  private static final String WHITESPACE_REGEX = "\\s+";

  private final CommandFactory commandFactory;

  public CommandExecutor(UserRepository userRepository, Map<String, User> activeUsers) {
    Validator.validateNullObject(Field.USER_REPOSITORY, userRepository);
    Validator.validateNullObject(Field.ACTIVE_USERS, activeUsers);

    this.commandFactory = new CommandFactory(userRepository, activeUsers);
  }

  CommandExecutor(CommandFactory commandFactory) {
    this.commandFactory = commandFactory;
  }

  public String execute(String commandLine, SelectionKey selectionKey) {
    if (commandLine.isBlank()) {
      return ServerMessage.BLANK_COMMAND.text();
    }

    String[] commandTokens = commandLine.split(WHITESPACE_REGEX);

    Command command = commandFactory.of(commandTokens[0]);
    if (command == null) {
      return ServerMessage.formatMessage(ServerMessage.INVALID_COMMAND, commandTokens[0]);
    }

    return command.execute(getCommandArguments(commandTokens), selectionKey);
  }

  private String[] getCommandArguments(String[] commandTokens) {
    if (commandTokens.length == 1) {
      return new String[0];
    }
    return Arrays.copyOfRange(commandTokens, 1, commandTokens.length);
  }
}
