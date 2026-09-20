package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import java.nio.channels.SelectionKey;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

public class RetrieveCredentialsCommand implements Command {
  private static final int WEBSITE_INDEX = 0;
  private static final int USER_INDEX = 1;

  @Override
  public String execute(String[] args, SelectionKey selectionKey) {
    if (args == null) {
      return ServerMessage.INVALID_ARGUMENTS.text();
    } else if (args.length != CommandType.RETRIEVE_CREDENTIALS.argsCount()) {
      return ServerMessage.formatWrongArgumentsCountMessage(CommandType.RETRIEVE_CREDENTIALS, args.length);
    }

    ClientSession clientSession = (ClientSession) selectionKey.attachment();
    User loggedUser = clientSession.getLoggedUser();

    if (loggedUser == null) {
      return ServerMessage.NOT_LOGGED_IN.text();
    }

    String password = loggedUser.retrieveCredentials(args[WEBSITE_INDEX], args[USER_INDEX]);
    if (password == null) {
      return ServerMessage.formatMessage(
          ServerMessage.INVALID_REGISTRATION, loggedUser.username(),
          args[WEBSITE_INDEX], args[USER_INDEX]);
    }

    return password;
  }
}
