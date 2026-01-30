package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.command.message.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.message.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

import java.nio.channels.SelectionKey;

public class RemovePasswordCommand implements Command {
    private static final int WEBSITE_INDEX = 0;
    private static final int USER_INDEX = 1;

    @Override
    public String execute(String[] args, SelectionKey selectionKey) {
        if (args == null) {
            return ServerMessage.INVALID_ARGUMENTS.text();
        } else if (args.length != CommandType.REMOVE_PASSWORD.argsCount()) {
            return ServerMessage.formatWrongArgumentsCountMessage(CommandType.REMOVE_PASSWORD, args.length);
        }

        ClientSession clientSession = (ClientSession) selectionKey.attachment();
        User loggedUser = clientSession.getLoggedUser();

        if (loggedUser == null) {
            return ServerMessage.NOT_LOGGED_IN.text();
        }

        if (!loggedUser.websiteRegistrationExists(args[WEBSITE_INDEX], args[USER_INDEX])) {
            return ServerMessage.formatMessage(ServerMessage.INVALID_REGISTRATION,
                loggedUser.username(), args[WEBSITE_INDEX], args[USER_INDEX]);
        }

        loggedUser.removeRegistration(args[WEBSITE_INDEX], args[USER_INDEX]);
        return ServerMessage.PASSWORD_REMOVED.text();
    }
}
