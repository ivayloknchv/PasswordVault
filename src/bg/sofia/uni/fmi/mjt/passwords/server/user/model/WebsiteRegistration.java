package bg.sofia.uni.fmi.mjt.passwords.server.user.model;

import java.io.Serial;
import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;

import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

public record WebsiteRegistration(String website, String user, String password) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1852650493366292040L;

  public static WebsiteRegistration of(String website, String user, String password) {
    Validator.validateString(Field.WEBSITE, website);
    Validator.validateString(Field.USER, user);
    Validator.validateString(Field.PASSWORD, password);

    return new WebsiteRegistration(
        website, user,
        new String(Base64.encodeBase64(password.getBytes())));
  }
}
