package bg.sofia.uni.fmi.mjt.passwords.server.checker;

import java.io.IOException;
import java.net.http.HttpResponse;

import org.apache.commons.codec.digest.DigestUtils;

import bg.sofia.uni.fmi.mjt.passwords.server.checker.client.EnzoicHttpClient;
import bg.sofia.uni.fmi.mjt.passwords.server.checker.dto.PasswordCandidates;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;
import bg.sofia.uni.fmi.mjt.passwords.server.log.Logger;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;
import com.google.gson.Gson;

public class CompromisedPasswordChecker implements PasswordChecker {

  private static final Gson GSON = new Gson();
  private final Logger logger;

  private static final int SUCCESS_CODE = 200;
  private static final int NOT_FOUND_CODE = 404;

  public CompromisedPasswordChecker() {
    this(Logger.getInstance());
  }

  CompromisedPasswordChecker(Logger logger) {
    this.logger = logger;
  }

  @Override
  public boolean isValid(String plainPassword) throws CompromisedPasswordCheckException {
    Validator.validateString(Field.PLAIN_PASSWORD, plainPassword);

    try (var enzoicHttpClient = new EnzoicHttpClient()) {
      HttpResponse<String> response = enzoicHttpClient.fetchPasswordsHashes(plainPassword);
      return checkPassword(response, plainPassword);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.log(e);
      throw new CompromisedPasswordCheckException("Password check failed", e);
    } catch (IOException e) {
      logger.log(e);
      throw new CompromisedPasswordCheckException("Password check failed", e);
    }
  }

  private boolean checkPassword(HttpResponse<String> response, String plainPassword) throws IOException {
    if (response.statusCode() == NOT_FOUND_CODE) {
      return true;
    } else if (response.statusCode() != SUCCESS_CODE) {
      logger.log("API request failed with code " + response.statusCode());
      throw new IOException("API request failed with code " + response.statusCode());
    }

    String passwordHash = DigestUtils.sha256Hex(plainPassword);
    PasswordCandidates passwordCandidates = GSON.fromJson(response.body(), PasswordCandidates.class);

    return passwordCandidates.candidates().stream().noneMatch(candidate -> candidate.sha256().equals(passwordHash));
  }
}
