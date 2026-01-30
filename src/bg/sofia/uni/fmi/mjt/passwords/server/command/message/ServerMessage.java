package bg.sofia.uni.fmi.mjt.passwords.server.command.message;

public enum ServerMessage {
    BLANK_COMMAND("Command line is blank"),
    INVALID_COMMAND("%s is not a valid command"),
    INVALID_ARGUMENTS("Invalid arguments"),
    WRONG_ARGUMENTS_COUNT("%s expected %d arguments but found %d"),
    LOGGED_IN_ANOTHER_ACCOUNT("User is logged in another account"),
    LOGGED_IN("User is logged in"),
    USER_EXISTS("User %s already exists"),
    NON_MATCHING_PASSWORDS("Passwords don't match"),
    REGISTERED("User %s has been registered"),
    NON_EXISTING_USER("User %s doesn't exist"),
    INCORRECT_PASSWORD("Incorrect password"),
    LOGIN("Login successful"),
    NOT_LOGGED_IN("User isn't logged in"),
    LOGOUT("Logout successful"),
    INVALID_REGISTRATION("User %s doesn't have registration for %s with username %s"),
    EXISTING_REGISTRATION("User %s already has registration for %s with username %s"),
    NOT_SECURE_PASSWORD("Password is not secure enough"),
    PASSWORD_ADDED("Password added"),
    PASSWORD_REMOVED("Password removed"),
    DISCONNECTED("Disconnected"),
    UNEXPECTED_SERVER_ERROR("Unexpected server error");

    private final String text;

    ServerMessage(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    public static String formatMessage(ServerMessage serverMessage, Object... fields) {
        return String.format(serverMessage.text(), fields);
    }

    public static String formatWrongArgumentsCountMessage(CommandType command, int passedArgCount) {
        return ServerMessage.formatMessage(ServerMessage.WRONG_ARGUMENTS_COUNT, command.commandName(),
            command.argsCount(), passedArgCount);
    }
}
