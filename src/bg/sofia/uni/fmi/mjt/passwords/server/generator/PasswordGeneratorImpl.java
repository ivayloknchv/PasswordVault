package bg.sofia.uni.fmi.mjt.passwords.server.generator;

import org.apache.commons.lang3.RandomStringUtils;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.PasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.CompromisedPasswordChecker;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.PasswordGenerateException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordGeneratorImpl implements PasswordGenerator {
    private static final int PASSWORD_LENGTH = 20;

    private static final int UPPERCASE_START = 65;
    private static final int UPPERCASE_END = 90;

    private static final int LOWERCASE_START = 97;
    private static final int LOWERCASE_END = 122;

    private static final int SPECIAL_CHAR_START = 33;
    private static final int SPECIAL_CHAR_END = 47;

    private static final int MIN_SYMBOLS_REQUIRED = 1;
    private static final int MAX_LETTERS_UPPERCASE = PASSWORD_LENGTH - 3;
    private static final int MAX_LETTERS_LOWERCASE = PASSWORD_LENGTH - 2;
    private static final int MAX_NUMBERS = PASSWORD_LENGTH - 1;

    private final PasswordChecker checker;

    public PasswordGeneratorImpl() {
        this(new CompromisedPasswordChecker());
    }

    PasswordGeneratorImpl(PasswordChecker checker) {
        this.checker = checker;
    }

    @Override
    public String generatePassword() throws PasswordGenerateException {
        try {
            return doGeneratePassword();
        } catch (CompromisedPasswordCheckException e) {
            throw new PasswordGenerateException("Password generation failed", e);
        }
    }

    private String doGeneratePassword() throws CompromisedPasswordCheckException {
        String password;

        do {
            int upperCaseLettersCount = getRandomNumber(MIN_SYMBOLS_REQUIRED, MAX_LETTERS_UPPERCASE);
            String upperCaseLetters =
                RandomStringUtils.random(upperCaseLettersCount, UPPERCASE_START, UPPERCASE_END, true, true);

            int lowerCaseLettersCount =
                getRandomNumber(MIN_SYMBOLS_REQUIRED, MAX_LETTERS_LOWERCASE - upperCaseLettersCount);
            String lowerCaseLetters =
                RandomStringUtils.random(lowerCaseLettersCount, LOWERCASE_START, LOWERCASE_END, true, true);

            int numbersCount =
                getRandomNumber(MIN_SYMBOLS_REQUIRED,
                    MAX_NUMBERS - upperCaseLettersCount - lowerCaseLettersCount);
            String numbers = RandomStringUtils.randomNumeric(numbersCount);

            int specialCharCount = PASSWORD_LENGTH - upperCaseLettersCount - lowerCaseLettersCount - numbersCount;
            String specialChar =
                RandomStringUtils.random(specialCharCount, SPECIAL_CHAR_START, SPECIAL_CHAR_END, false, false);

            password = concatenatePassword(upperCaseLetters, lowerCaseLetters, numbers, specialChar);
        } while (!checker.isValid(password));

        return password;
    }

    private String concatenatePassword(String upperCaseLetters, String lowerCaseLetters, String numbers,
                                       String specialChar) {
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers).concat(specialChar);

        List<Character> characters = combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(characters);

        return characters.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private static int getRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }
}
