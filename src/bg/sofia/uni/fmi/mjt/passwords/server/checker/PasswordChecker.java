package bg.sofia.uni.fmi.mjt.passwords.server.checker;

import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;

public interface PasswordChecker {

    boolean isValid(String plainPassword) throws CompromisedPasswordCheckException;
}
