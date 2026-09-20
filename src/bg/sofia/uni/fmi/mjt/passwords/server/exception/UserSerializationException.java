package bg.sofia.uni.fmi.mjt.passwords.server.exception;

public class UserSerializationException extends RuntimeException {
  public UserSerializationException(String message) {
    super(message);
  }

  public UserSerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
