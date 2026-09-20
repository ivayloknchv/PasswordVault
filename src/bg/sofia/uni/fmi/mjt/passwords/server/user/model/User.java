package bg.sofia.uni.fmi.mjt.passwords.server.user.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

public class User implements Serializable {
  @Serial
  private static final long serialVersionUID = -4022049254354164760L;

  private final String username;
  private final String password;
  private final Map<String, List<WebsiteRegistration>> registrations;

  public User(String username, String password) {
    Validator.validateString(Field.USERNAME, username);
    Validator.validateString(Field.PASSWORD, password);

    this.username = username;
    this.password = DigestUtils.sha256Hex(password);
    this.registrations = new HashMap<>();
  }

  public String username() {
    return username;
  }

  public boolean isPasswordValid(String plainPassword) {
    Validator.validateString(Field.PLAIN_PASSWORD, plainPassword);

    return DigestUtils.sha256Hex(plainPassword).equals(this.password);
  }

  public void addWebsiteRegistration(WebsiteRegistration websiteRegistration) {
    Validator.validateNullObject(Field.WEBSITE_REGISTRATION, websiteRegistration);

    registrations.putIfAbsent(websiteRegistration.website(), new ArrayList<>());
    registrations.get(websiteRegistration.website()).add(websiteRegistration);
  }

  public void removeRegistration(String website, String user) {
    Validator.validateString(Field.WEBSITE, website);
    Validator.validateString(Field.USER, user);

    List<WebsiteRegistration> websiteRegistrations = registrations.get(website);
    if (websiteRegistrations == null) {
      return;
    }

    websiteRegistrations.removeIf(r -> r.website().equals(website) && r.user().equals(user));
  }

  public String retrieveCredentials(String website, String user) {
    Validator.validateString(Field.WEBSITE, website);
    Validator.validateString(Field.USER, user);

    List<WebsiteRegistration> websiteRegistrations = registrations.get(website);
    if (websiteRegistrations == null) {
      return null;
    }

    return websiteRegistrations
        .stream()
        .filter(r -> r.user().equals(user))
        .map(r -> new String(Base64.decodeBase64(r.password().getBytes())))
        .findFirst()
        .orElse(null);
  }

  public boolean websiteRegistrationExists(String website, String user) {
    Validator.validateString(Field.WEBSITE, website);
    Validator.validateString(Field.USER, user);

    List<WebsiteRegistration> websiteRegistrations = registrations.get(website);
    if (websiteRegistrations == null) {
      return false;
    }

    return websiteRegistrations.stream().anyMatch(r -> r.user().equals(user));
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(username, user.username) &&
        Objects.equals(password, user.password) &&
        Objects.equals(registrations, user.registrations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, registrations);
  }
}
