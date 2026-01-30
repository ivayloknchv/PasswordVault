package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.command.message.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.message.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class LogoutCommand implements Command {
    Map<String, User> activeUsers;

    public LogoutCommand(Map<String, User> activeUsers) {
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute(String[] args, SelectionKey selectionKey) {
        if (args == null) {
            return ServerMessage.INVALID_ARGUMENTS.text();
        } else if (args.length != CommandType.LOGOUT.argsCount()) {
            return ServerMessage.formatWrongArgumentsCountMessage(CommandType.LOGOUT, args.length);
        }

        ClientSession clientSession = (ClientSession) selectionKey.attachment();
        User loggedUser = clientSession.getLoggedUser();

        if (loggedUser == null) {
            return ServerMessage.NOT_LOGGED_IN.text();
        }

        activeUsers.remove(loggedUser.username());
        clientSession.setLoggedUser(null);

        return ServerMessage.LOGOUT.text();
    }
}
