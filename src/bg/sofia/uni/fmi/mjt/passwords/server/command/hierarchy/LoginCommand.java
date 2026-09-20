package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import java.nio.channels.SelectionKey;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

public class LoginCommand implements Command {
  private static final int USER_INDEX = 0;
  private static final int PASSWORD_INDEX = 1;

  UserRepository userRepository;
  Map<String, User> activeUsers;

  public LoginCommand(UserRepository userRepository, Map<String, User> activeUsers) {
    Validator.validateNullObject(Field.USER_REPOSITORY, userRepository);
    Validator.validateNullObject(Field.ACTIVE_USERS, activeUsers);

    this.userRepository = userRepository;
    this.activeUsers = activeUsers;
  }

  @Override
  public String execute(String[] args, SelectionKey selectionKey) {
    if (args == null) {
      return ServerMessage.INVALID_ARGUMENTS.text();
    } else if (args.length != CommandType.LOGIN.argsCount()) {
      return ServerMessage.formatWrongArgumentsCountMessage(CommandType.LOGIN, args.length);
    }

    ClientSession clientSession = (ClientSession) selectionKey.attachment();
    User loggedUser = clientSession.getLoggedUser();
    if (loggedUser != null) {
      return ServerMessage.LOGGED_IN_ANOTHER_ACCOUNT.text();
    }

    if (activeUsers.containsKey(args[USER_INDEX])) {
      return ServerMessage.LOGGED_IN.text();
    }

    User user = userRepository.getUser(args[USER_INDEX]);
    if (user == null) {
      return ServerMessage.formatMessage(ServerMessage.NON_EXISTING_USER, args[0]);
    }

    if (!user.isPasswordValid(args[PASSWORD_INDEX])) {
      return ServerMessage.INCORRECT_PASSWORD.text();
    }

    clientSession.setLoggedUser(user);
    activeUsers.put(user.username(), user);
    return ServerMessage.LOGIN.text();
  }
}
