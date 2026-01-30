package bg.sofia.uni.fmi.mjt.passwords.server.user.repository;

import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    @Test
    void testAddUserNull() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryUserRepository(new HashMap<>()).addUser(null),
            "Should thrown an exception when user is null");
    }

    @Test
    void testAddUser() {
        UserRepository repo = new InMemoryUserRepository(new HashMap<>());
        repo.addUser(new User("name", "pass"));

        assertTrue(repo.userExists("name"), "User with such name isn't added");
    }

    @Test
    void testGetUserNull() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryUserRepository(new HashMap<>()).getUser(null),
            "Should thrown an exception when username is null");
    }

    @Test
    void testGetUserBlank() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryUserRepository(new HashMap<>()).getUser(" "),
            "Should thrown an exception when username is blank");
    }

    @Test
    void testUserExistsNull() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryUserRepository(new HashMap<>()).userExists(null),
            "Should thrown an exception when username is null");
    }

    @Test
    void testUserExistsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryUserRepository(new HashMap<>()).userExists(""),
            "Should thrown an exception when username is blank");
    }

    @Test
    void testUserExistsTrue() {
        UserRepository repo = new InMemoryUserRepository(new HashMap<>());
        repo.addUser(new User("name", "pass"));

        assertTrue(repo.userExists("name"), "User with such name exists");
    }

    @Test
    void testUserExistsFalse() {
        UserRepository repo = new InMemoryUserRepository(new HashMap<>());
        repo.addUser(new User("name", "pass"));

        assertFalse(repo.userExists("Name"), "User with such name doesn't");
    }
}