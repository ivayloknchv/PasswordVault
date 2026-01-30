package bg.sofia.uni.fmi.mjt.passwords.server.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static User user;

    @BeforeEach
    void setUp() {
        user = new User("user", "123456");
        user.addWebsiteRegistration(WebsiteRegistration.of("gmail", "email1@gmail.com", "123456"));
        user.addWebsiteRegistration(WebsiteRegistration.of("gmail", "email2@gmail.com", "123456"));
        user.addWebsiteRegistration(WebsiteRegistration.of("instagram", "user", "123456"));
    }

    @Test
    void testCreateUserNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> new User(null, "pass"),
            "Should thrown an exception when username is null");
    }

    @Test
    void testCreateUserBlankUsername() {
        assertThrows(IllegalArgumentException.class, () -> new User("", "pass"),
            "Should thrown an exception when username is blank");
    }

    @Test
    void testCreateUserNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> new User("user", null),
            "Should thrown an exception when password is null");
    }

    @Test
    void testCreateUserBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> new User("user", ""),
            "Should thrown an exception when password is blank");
    }

    @Test
    void testIsPasswordValidNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> user.isPasswordValid(null),
            "Should thrown an exception when password is null");
    }

    @Test
    void testIsPasswordValidBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> user.isPasswordValid(null),
            "Should thrown an exception when password is null");
    }

    @Test
    void testIsPasswordValidTrue() {
        assertTrue(user.isPasswordValid("123456"), "Should thrown an exception when password matches user's password");
    }

    @Test
    void testIsPasswordValidFalse() {
        assertFalse(user.isPasswordValid("123455"),
            "Should thrown an exception when password doesn't match user's password");
    }

    @Test
    void testAddWebsiteRegistrationNull() {
        assertThrows(IllegalArgumentException.class, () -> user.addWebsiteRegistration(null),
            "Website registration is null");
    }

    @Test
    void testAddWebsiteRegistration() {
        user.addWebsiteRegistration(WebsiteRegistration.of("facebook", "My Name", "123456"));
        assertTrue(user.websiteRegistrationExists("facebook", "My Name"), "The registration is added to the user");
    }

    @Test
    void testRemoveRegistrationNullWebsite() {
        assertThrows(IllegalArgumentException.class, () -> user.removeRegistration(null, "account"),
            "Should thrown an exception when website is null");
    }

    @Test
    void testRemoveRegistrationBlankWebsite() {
        assertThrows(IllegalArgumentException.class, () -> user.removeRegistration("", "account"),
            "Should thrown an exception when website is blank");
    }

    @Test
    void testRemoveRegistrationNullUser() {
        assertThrows(IllegalArgumentException.class, () -> user.removeRegistration("website", null),
            "Should thrown an exception when user is null");
    }

    @Test
    void testRemoveRegistrationBlankUser() {
        assertThrows(IllegalArgumentException.class, () -> user.removeRegistration("website", "    "),
            "Should thrown an exception when user is blank");
    }

    @Test
    void testRemoveRegistrationExistingRegistration() {
        assertTrue(user.websiteRegistrationExists("gmail", "email1@gmail.com"), "Registration exists in the beginning");
        user.removeRegistration("gmail", "email1@gmail.com");
        assertFalse(user.websiteRegistrationExists("gmail", "email1@gmail.com"), "Registration is removed");
    }

    @Test
    void testRemoveRegistrationNonExistingRegistration() {
        assertFalse(user.websiteRegistrationExists("gmail", "email@gmail.com"),
            "Registration doesn't exist in the beginning");
        user.removeRegistration("gmail", "email1@gmail.com");
        assertFalse(user.websiteRegistrationExists("gmail", "email@gmail.com"), "Registration still doesn't exist");
    }

    @Test
    void testRetrieveCredentialsNullWebsite() {
        assertThrows(IllegalArgumentException.class, () -> user.retrieveCredentials(null, "user"),
            "Should thrown an exception when website is null");
    }

    @Test
    void testRetrieveCredentialsBlankWebsite() {
        assertThrows(IllegalArgumentException.class, () -> user.retrieveCredentials("\n", "user"),
            "Should thrown an exception when website is blank");
    }

    @Test
    void testRetrieveCredentialsNullUser() {
        assertThrows(IllegalArgumentException.class, () -> user.retrieveCredentials("website", null),
            "Should thrown an exception when user is null");
    }

    @Test
    void testRetrieveCredentialsBlankUser() {
        assertThrows(IllegalArgumentException.class, () -> user.retrieveCredentials("website", "      "),
            "User is blank");
    }

    @Test
    void testRetrieveCredentialsUnknownWebsite() {
        assertNull(user.retrieveCredentials("twitter", "user"), "Registrations for twitter don't exist");
    }

    @Test
    void testRetrieveCredentialsValidRegistration() {
        assertEquals("123456", user.retrieveCredentials("instagram", "user"),
            "User has an existing Instagram registration");
    }

    @Test
    void testWebsiteRegistrationExistsNullWebsite() {
        assertThrows(IllegalArgumentException.class, () -> user.websiteRegistrationExists(null, "user"),
            "Should thrown an exception when website is null");
    }

    @Test
    void testWebsiteRegistrationExistsBlankWebsite() {
        assertThrows(IllegalArgumentException.class, () -> user.websiteRegistrationExists("\n", "user"),
            "Should thrown an exception when website is blank");
    }

    @Test
    void testWebsiteRegistrationExistsNullUser() {
        assertThrows(IllegalArgumentException.class, () -> user.websiteRegistrationExists("website", null),
            "Should thrown an exception when user is null");
    }

    @Test
    void testWebsiteRegistrationExistsBlankUser() {
        assertThrows(IllegalArgumentException.class, () -> user.websiteRegistrationExists("website", "      "),
            "Should thrown an exception when user is blank");
    }

    @Test
    void testWebsiteRegistrationExistsTrue() {
        assertTrue(user.websiteRegistrationExists("instagram", "user"), "User has an existing Instagram registration");
    }

    @Test
    void testWebsiteRegistrationExistsFalse() {
        assertFalse(user.websiteRegistrationExists("twitter", "user"), "Registrations for twitter don't exist");
    }
}