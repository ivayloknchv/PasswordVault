package bg.sofia.uni.fmi.mjt.passwords.server.checker.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;

import bg.sofia.uni.fmi.mjt.passwords.server.checker.ApiKeys;

public class EnzoicHttpClient implements AutoCloseable, EnzoicClient {
  private static final String ENDPOINT = "https://api.enzoic.com/v1/passwords";

  private static final Path DEFAULT_API_KEYS_PATH = Path.of("credentials.json");

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String AUTHORIZATION_TYPE = "Basic %s";

  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String CONTENT_TYPE_VALUE = "application/json";

  private static final String REQUEST_BODY =
      """
          {
            "partialSHA256": "%s"
          }
          """;

  private static final int REQUIRED_CHARACTERS_COUNT = 10;

  private final HttpClient httpClient;

  public EnzoicHttpClient() {
    this(HttpClient.newHttpClient());
  }

  EnzoicHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public HttpResponse<String> fetchPasswordsHashes(String plainPassword) throws IOException, InterruptedException {
    String passwordHash = DigestUtils.sha256Hex(plainPassword);
    HttpRequest request = buildHttpRequest(passwordHash);
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private HttpRequest buildHttpRequest(String passwordHash) {
    return HttpRequest.newBuilder()
        .header(AUTHORIZATION_HEADER, formatAuthorizationHeader())
        .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
        .uri(URI.create(ENDPOINT))
        .POST(HttpRequest.BodyPublishers.ofString(formatRequestBody(passwordHash)))
        .build();
  }

  private String formatRequestBody(String passwordHash) {
    return String.format(REQUEST_BODY, passwordHash.substring(0, REQUIRED_CHARACTERS_COUNT));
  }

  private String formatAuthorizationHeader() {
    ApiKeys apiKeys = ApiKeys.of(DEFAULT_API_KEYS_PATH);
    return AUTHORIZATION_TYPE
        .formatted(Base64.getEncoder()
            .encodeToString((apiKeys.key() + ":" + apiKeys.secret())
                .getBytes()));
  }

  @Override
  public void close() {
    if (httpClient != null) {
      httpClient.close();
    }
  }
}
