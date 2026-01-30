package bg.sofia.uni.fmi.mjt.passwords.server.checker;

import bg.sofia.uni.fmi.mjt.passwords.server.checker.dto.PasswordCheckerDTO;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;
import bg.sofia.uni.fmi.mjt.passwords.server.log.Logger;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Base64;

public class CompromisedPasswordChecker implements PasswordChecker {
    private static final String ENDPOINT = "https://api.enzoic.com/v1/passwords";

    private static final Path DEFAULT_API_KEYS_PATH = Path.of("authentication.json");

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_TYPE = "Basic %s";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";

    private static final String REQUEST_BODY = """
        {
          "partialSHA256": "%s"
        }
        """;

    private static final int REQUIRED_CHARACTERS_COUNT = 10;

    private static final int SUCCESS_CODE = 200;
    private static final int NOT_FOUND_CODE = 404;

    private final Logger logger;
    private final ApiKeys apiKeys;
    private final HttpClient httpClient;

    public CompromisedPasswordChecker() {
        this(ApiKeys.of(DEFAULT_API_KEYS_PATH), HttpClient.newHttpClient(), Logger.getInstance());
    }

    CompromisedPasswordChecker(ApiKeys apiKeys, HttpClient httpClient, Logger logger) {
        Validator.validateNullObject(Field.API_KEYS, apiKeys);
        Validator.validateNullObject(Field.HTTP_CLIENT, httpClient);

        this.apiKeys = apiKeys;
        this.httpClient = httpClient;
        this.logger = logger;
    }

    @Override
    public boolean isValid(String plainPassword) throws CompromisedPasswordCheckException {
        Validator.validateString(Field.PLAIN_PASSWORD, plainPassword);

        try {
            String hashedPassword = DigestUtils.sha256Hex(plainPassword);
            HttpRequest request = buildHttpRequest(hashedPassword);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return handleHttpResponse(response, hashedPassword);
        } catch (IOException | InterruptedException e) {
            logger.log(e);
            throw new CompromisedPasswordCheckException("Password check failed", e);
        }
    }

    private HttpRequest buildHttpRequest(String hashedPassword) {
        return HttpRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, formatAuthorizationHeader())
            .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
            .uri(URI.create(ENDPOINT))
            .POST(HttpRequest.BodyPublishers.ofString(
                formatRequestBody(hashedPassword)))
            .build();
    }

    private String formatRequestBody(String hashedPassword) {
        return String.format(REQUEST_BODY, hashedPassword.substring(0, REQUIRED_CHARACTERS_COUNT));
    }

    private String formatAuthorizationHeader() {
        return AUTHORIZATION_TYPE.formatted(Base64.getEncoder()
            .encodeToString((apiKeys.key() + ":" + apiKeys.secret()).getBytes()));
    }

    private boolean handleHttpResponse(HttpResponse<String> response, String hashedPassword)
        throws CompromisedPasswordCheckException {
        if (response.statusCode() == NOT_FOUND_CODE) {
            return true;
        } else if (response.statusCode() != SUCCESS_CODE) {
            logger.log("API request failed with code " + response.statusCode());
            throw new CompromisedPasswordCheckException("Password check failed");
        }

        PasswordCheckerDTO passwordCheckerDTO =
            new Gson().fromJson(response.body(), PasswordCheckerDTO.class);

        return passwordCheckerDTO
            .candidates()
            .stream()
            .noneMatch(candidate -> candidate.sha256().equals(hashedPassword));
    }
}
