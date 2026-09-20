package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import java.nio.channels.SelectionKey;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.CompromisedPasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.PasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

public class RegisterCommand implements Command {
  private static final int USER_INDEX = 0;
  private static final int PASSWORD_INDEX = 1;
  private static final int PASSWORD_REPEAT_INDEX = 2;

  private final UserRepository userRepository;
  private final PasswordChecker passwordChecker;

  public RegisterCommand(UserRepository userRepository) {
    Validator.validateNullObject(Field.USER_REPOSITORY, userRepository);

    this(userRepository, new CompromisedPasswordChecker());
  }

  RegisterCommand(UserRepository userRepository, PasswordChecker passwordChecker) {
    this.userRepository = userRepository;
    this.passwordChecker = passwordChecker;
  }

  @Override
  public String execute(String[] args, SelectionKey selectionKey) {
    if (args == null) {
      return ServerMessage.INVALID_ARGUMENTS.text();
    } else if (args.length != CommandType.REGISTER.argsCount()) {
      return ServerMessage.formatWrongArgumentsCountMessage(
          CommandType.REGISTER,
          args.length);
    }

    ClientSession clientSession = (ClientSession) selectionKey.attachment();
    User loggedUser = clientSession.getLoggedUser();

    if (loggedUser != null) {
      return ServerMessage.LOGGED_IN.text();
    }

    if (userRepository.getUser(args[USER_INDEX]) != null) {
      return ServerMessage.formatMessage(ServerMessage.USER_EXISTS, args[USER_INDEX]);
    }

    if (!args[PASSWORD_INDEX].equals(args[PASSWORD_REPEAT_INDEX])) {
      return ServerMessage.NON_MATCHING_PASSWORDS.text();
    }

    try {
      if (!passwordChecker.isValid(args[PASSWORD_INDEX])) {
        return ServerMessage.NOT_SECURE_PASSWORD.text();
      }
    } catch (Exception e) {
      return ServerMessage.formatMessage(ServerMessage.UNEXPECTED_SERVER_ERROR, e.getMessage());
    }

    User newUser = new User(args[USER_INDEX], args[PASSWORD_INDEX]);
    userRepository.addUser(newUser);

    return ServerMessage.formatMessage(ServerMessage.REGISTERED, args[USER_INDEX]);
  }
}
