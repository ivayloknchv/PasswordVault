package bg.sofia.uni.fmi.mjt.passwords.server.generator;

import bg.sofia.uni.fmi.mjt.passwords.server.exception.PasswordGenerateException;

public interface PasswordGenerator {
  String generatePassword() throws PasswordGenerateException;
}
