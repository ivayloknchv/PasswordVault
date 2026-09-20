package bg.sofia.uni.fmi.mjt.passwords.server.user.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

public class InMemoryUserRepository implements UserRepository {
    private Map<String, User> users = new ConcurrentHashMap<>();

    private static UserRepository repoInstance;

    InMemoryUserRepository(Map<String, User> users) {
        this.users = users;
    }

    private InMemoryUserRepository() {
    }

    public static UserRepository getInstance() {
        if (repoInstance == null) {
            synchronized (InMemoryUserRepository.class) {
                if (repoInstance == null) {
                    repoInstance = new InMemoryUserRepository();
                }
            }
        }
        return repoInstance;
    }

    @Override
    public void addUser(User user) {
        Validator.validateNullObject(Field.USER, user);

        users.put(user.username(), user);
    }

    @Override
    public User getUser(String username) {
        Validator.validateString(Field.USERNAME, username);

        return users.get(username);
    }

    @Override
    public Collection<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public boolean userExists(String username) {
        Validator.validateString(Field.USERNAME, username);

        return users.containsKey(username);
    }
}
