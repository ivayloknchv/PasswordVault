package bg.sofia.uni.fmi.mjt.passwords.server.exception;

public class CompromisedPasswordCheckException extends Exception {
    public CompromisedPasswordCheckException(String message) {
        super(message);
    }

    public CompromisedPasswordCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
