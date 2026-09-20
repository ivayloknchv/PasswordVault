package bg.sofia.uni.fmi.mjt.passwords.server.util;

public class Validator {

  private Validator() {
  }

  public static void validateString(Field field, String string) {
    if (string == null) {
      throw new IllegalArgumentException(field + " is null.");
    } else if (string.isBlank()) {
      throw new IllegalArgumentException(field + " is blank.");
    }
  }

  public static <T> void validateNullObject(Field field, T object) {
    if (object == null) {
      throw new IllegalArgumentException(field + " is null.");
    }
  }
}
