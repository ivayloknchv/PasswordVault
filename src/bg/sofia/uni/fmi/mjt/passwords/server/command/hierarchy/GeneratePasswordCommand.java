package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import java.nio.channels.SelectionKey;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.PasswordGenerateException;
import bg.sofia.uni.fmi.mjt.passwords.server.generator.PasswordGenerator;
import bg.sofia.uni.fmi.mjt.passwords.server.generator.PasswordGeneratorImpl;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.WebsiteRegistration;

public class GeneratePasswordCommand implements Command {
  private static final int WEBSITE = 0;
  private static final int USER = 1;

  private final PasswordGenerator passwordGenerator;

  public GeneratePasswordCommand() {
    this(new PasswordGeneratorImpl());
  }

  GeneratePasswordCommand(PasswordGenerator passwordGenerator) {
    this.passwordGenerator = passwordGenerator;
  }

  @Override
  public String execute(String[] args, SelectionKey selectionKey) {
    if (args == null) {
      return ServerMessage.INVALID_ARGUMENTS.text();
    } else if (args.length != CommandType.GENERATE_PASSWORD.argsCount()) {
      return ServerMessage.formatWrongArgumentsCountMessage(CommandType.GENERATE_PASSWORD, args.length);
    }

    ClientSession clientSession = (ClientSession) selectionKey.attachment();
    User loggedUser = clientSession.getLoggedUser();

    if (loggedUser == null) {
      return ServerMessage.NOT_LOGGED_IN.text();
    }

    if (loggedUser.websiteRegistrationExists(args[WEBSITE], args[USER])) {
      return ServerMessage.formatMessage(
          ServerMessage.EXISTING_REGISTRATION, loggedUser.username(),
          args[WEBSITE], args[USER]);
    }

    try {
      String password = passwordGenerator.generatePassword();
      loggedUser.addWebsiteRegistration(WebsiteRegistration.of(args[WEBSITE], args[USER], password));
      return password;
    } catch (PasswordGenerateException e) {
      return ServerMessage.formatMessage(ServerMessage.UNEXPECTED_SERVER_ERROR, e.getMessage());
    }
  }
}
