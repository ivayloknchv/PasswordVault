package bg.sofia.uni.fmi.mjt.passwords.server.exception;

public class PasswordGenerateException extends Exception {
    public PasswordGenerateException(String message) {
        super(message);
    }

    public PasswordGenerateException(String message, Throwable cause) {
        super(message, cause);
    }
}
