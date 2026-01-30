package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.ClientSession;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.PasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.CompromisedPasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.command.message.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.message.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.WebsiteRegistration;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

import java.nio.channels.SelectionKey;

public class AddPasswordCommand implements Command {
    private static final int WEBSITE_INDEX = 0;
    private static final int USER_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    private final PasswordChecker checker;

    public AddPasswordCommand() {
        this(new CompromisedPasswordChecker());
    }

    AddPasswordCommand(PasswordChecker checker) {
        this.checker = checker;
    }

    @Override
    public String execute(String[] args, SelectionKey selectionKey) {
        if (args == null) {
            return ServerMessage.INVALID_ARGUMENTS.text();
        } else if (args.length != CommandType.ADD_PASSWORD.argsCount()) {
            return ServerMessage.formatWrongArgumentsCountMessage(CommandType.ADD_PASSWORD, args.length);
        }

        ClientSession clientSession = (ClientSession) selectionKey.attachment();
        User loggedUser = clientSession.getLoggedUser();
        if (loggedUser == null) {
            return ServerMessage.NOT_LOGGED_IN.text();
        }

        if (loggedUser.websiteRegistrationExists(args[WEBSITE_INDEX], args[USER_INDEX])) {
            return ServerMessage.formatMessage(ServerMessage.EXISTING_REGISTRATION, loggedUser.username(),
                args[WEBSITE_INDEX], args[USER_INDEX]);
        }

        try {
            if (!checker.isValid(args[PASSWORD_INDEX])) {
                return ServerMessage.NOT_SECURE_PASSWORD.text();
            }
            loggedUser.addWebsiteRegistration(
                WebsiteRegistration.of(args[WEBSITE_INDEX], args[USER_INDEX], args[PASSWORD_INDEX]));
            return ServerMessage.PASSWORD_ADDED.text();
        } catch (Exception e) {
            return ServerMessage.UNEXPECTED_SERVER_ERROR.text();
        }
    }
}
