package bg.sofia.uni.fmi.mjt.passwords.server.exception;

public class UserDeserializationException extends RuntimeException {
  public UserDeserializationException(String message) {
    super(message);
  }

  public UserDeserializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
