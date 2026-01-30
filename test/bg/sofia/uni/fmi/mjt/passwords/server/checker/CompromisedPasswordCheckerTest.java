package bg.sofia.uni.fmi.mjt.passwords.server.checker;

import bg.sofia.uni.fmi.mjt.passwords.server.log.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.CompromisedPasswordCheckException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompromisedPasswordCheckerTest {

    private static HttpClient clientMock;
    private static HttpResponse<String> responseMock;
    private static PasswordChecker passwordChecker;

    private static final String API_RESPONSE = """
        {
           "candidates":[
              {
                 "sha256":"a3f9c2d8e1gvdhj021300320021ejndbnwbej7328382631bhdsbds123823323d"
              },
              {
                 "sha256":"932b004070f4e21226b59f039b437cd2ffa7d31da0baef37cab15c7732ece352"
              }
           ]
        }
        """;

    @BeforeAll
    static void setUp() {
        clientMock = mock(HttpClient.class);
        responseMock = mock(HttpResponse.class);
        passwordChecker = new CompromisedPasswordChecker(new ApiKeys("key", "secret"), clientMock, mock(Logger.class));
    }

    @Test
    void testIsValidNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> passwordChecker.isValid(null),
            "Should thrown an exception when password is null");
    }

    @Test
    void testIsValidBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> passwordChecker.isValid(""),
            "Should thrown an exception when password is blank");
    }

    @Test
    void testIsValidTrueNotFound() throws IOException, InterruptedException, CompromisedPasswordCheckException {
        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(404);

        assertTrue(passwordChecker.isValid("dummyPassword"));
    }

    @Test
    void testIsValidUnexpectedError() throws IOException, InterruptedException {
        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(500);

        assertThrows(CompromisedPasswordCheckException.class, () -> passwordChecker.isValid("password"),
            "Unexpected error occurred while fetching");
    }

    @Test
    void testIsValidFalse() throws IOException, InterruptedException, CompromisedPasswordCheckException {
        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.body()).thenReturn(API_RESPONSE);

        assertFalse(passwordChecker.isValid("dummyPassword"), "Password is in the list of compromised password");
    }

    @Test
    void testIsValidTrueNotFoundInList() throws IOException, InterruptedException, CompromisedPasswordCheckException {
        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.body()).thenReturn(API_RESPONSE);

        assertTrue(passwordChecker.isValid("dummyPass"), "Password is in the list of compromised password");
    }
}