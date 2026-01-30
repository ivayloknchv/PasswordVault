package bg.sofia.uni.fmi.mjt.passwords.server.command.hierarchy;

import java.nio.channels.SelectionKey;

public interface Command {
    String execute(String[] args, SelectionKey selectionKey);
}
