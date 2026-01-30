package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import bg.sofia.uni.fmi.mjt.passwords.server.command.message.CommandType;
import bg.sofia.uni.fmi.mjt.passwords.server.command.message.ServerMessage;

import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand implements Command {

    @Override
    public String execute(String[] args, SelectionKey selectionKey) {
        if (args == null) {
            return ServerMessage.INVALID_ARGUMENTS.text();
        } else if (args.length != CommandType.HELP.argsCount()) {
            return ServerMessage.formatWrongArgumentsCountMessage(CommandType.HELP, args.length);
        }

        return Arrays.stream(CommandType.values()).map(CommandType::info)
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
