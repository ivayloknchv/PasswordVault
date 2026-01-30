package bg.sofia.uni.fmi.mjt.passwords.server.user.repository;

import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

import java.util.Collection;

public interface UserRepository {

    void addUser(User user);

    User getUser(String username);

    Collection<User> getAllUsers();

    boolean userExists(String username);
}
