package bg.sofia.uni.fmi.mjt.passwords.server.command.message;

import java.util.Arrays;

public enum CommandType {
    REGISTER("register", 3, "register <user> <password> <password-repeat>"),
    LOGIN("login", 2, "login <user> <password>"),
    LOGOUT("logout", 0, "logout"),
    RETRIEVE_CREDENTIALS("retrieve-credentials", 2, "retrieve-credentials <website> <user>"),
    GENERATE_PASSWORD("generate-password", 2, "generate-password <website> <user>"),
    ADD_PASSWORD("add-password", 3, "add-password <website> <user> <password>"),
    REMOVE_PASSWORD("remove-password", 2, "remove-password <website> <user>"),
    DISCONNECT("disconnect", 0, "disconnect"),
    HELP("help", 0, "help");

    private final String commandName;
    private final int argsCount;
    private final String info;

    CommandType(String commandName, int argsCount, String info) {
        this.commandName = commandName;
        this.argsCount = argsCount;
        this.info = info;
    }

    public String commandName() {
        return commandName;
    }

    public int argsCount() {
        return argsCount;
    }

    public String info() {
        return info;
    }

    public static CommandType getFromString(String commandTypeName) {
        return Arrays.stream(values())
            .filter(commandType -> commandType.commandName.equals(commandTypeName))
            .findFirst().orElse(null);
    }
}
