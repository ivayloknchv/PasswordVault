package bg.sofia.uni.fmi.mjt.passwords.server.command;

import bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy.Command;
import bg.sofia.uni.fmi.mjt.passwords.server.command.message.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandExecutor {
    private static final String WHITESPACE_REGEX = "\\s+";

    private final UserRepository userRepository;
    private final Map<String, User> activeUsers;

    public CommandExecutor(UserRepository userRepository) {
        this(userRepository, new ConcurrentHashMap<>());
    }

    CommandExecutor(UserRepository userRepository, Map<String, User> activeUsers) {
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }

    public String execute(String commandLine, SelectionKey selectionKey) {
        if (commandLine.isBlank()) {
            return ServerMessage.BLANK_COMMAND.text();
        }

        String[] commandTokens = commandLine.split(WHITESPACE_REGEX);

        Command command = CommandFactory.of(commandTokens[0], userRepository, activeUsers);
        if (command == null) {
            return ServerMessage.formatMessage(ServerMessage.INVALID_COMMAND, commandTokens[0]);
        }

        return command.execute(getCommandArguments(commandTokens), selectionKey);
    }

    private static String[] getCommandArguments(String[] commandTokens) {
        if (commandTokens.length == 1) {
            return new String[0];
        }
        return Arrays.copyOfRange(commandTokens, 1, commandTokens.length);
    }
}
