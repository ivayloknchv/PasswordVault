package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import java.nio.channels.SelectionKey;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

public class DisconnectCommand implements Command {
  private final Map<String, User> activeUsers;

  public DisconnectCommand(Map<String, User> activeUsers) {
    Validator.validateNullObject(Field.ACTIVE_USERS, activeUsers);

    this.activeUsers = activeUsers;
  }

  @Override
  public String execute(String[] args, SelectionKey selectionKey) {
    if (args == null) {
      return ServerMessage.INVALID_ARGUMENTS.text();
    } else if (args.length != CommandType.DISCONNECT.argsCount()) {
      return ServerMessage.formatWrongArgumentsCountMessage(CommandType.DISCONNECT, args.length);
    }

    ClientSession clientSession = (ClientSession) selectionKey.attachment();
    User loggedUser = clientSession.getLoggedUser();

    if (loggedUser != null) {
      activeUsers.remove(loggedUser.username());
      clientSession.setLoggedUser(null);
    }

    return ServerMessage.DISCONNECTED.text();
  }
}
