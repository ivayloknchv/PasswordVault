package bg.sofia.uni.fmi.mjt.passwords.server.util;

public enum Field {
  USER("User"),
  USERS("Users"),
  ACTIVE_USERS("Active users"),
  USER_REPOSITORY("User repository"),
  USERNAME("Username"),
  WEBSITE("Website"),
  WEBSITE_REGISTRATION("Website registration"),
  REGISTRATIONS("Registrations"),
  PASSWORD("Password"),
  PLAIN_PASSWORD("Plain password"),
  API_KEYS("API keys"),
  HTTP_CLIENT("HTTP client"),
  PATH("Path"),
  READER("Reader");

  private final String name;

  Field(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
