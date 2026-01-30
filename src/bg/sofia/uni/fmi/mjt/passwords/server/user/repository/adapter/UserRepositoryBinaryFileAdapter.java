package bg.sofia.uni.fmi.mjt.passwords.server.user.repository.adapter;

import bg.sofia.uni.fmi.mjt.passwords.server.exception.UserDeserializationException;
import bg.sofia.uni.fmi.mjt.passwords.server.exception.UserSerializationException;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UserRepositoryBinaryFileAdapter implements UserRepositoryFileAdapter {
    private static final Path USERS_DIRECTORY = Path.of("data");
    private static final Path USERS_FILE = Path.of(USERS_DIRECTORY + File.separator + "users.dat");

    @Override
    public void serializeRepository(UserRepository userRepository) {
        Validator.validateNullObject(Field.USER_REPOSITORY, userRepository);

        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(USERS_FILE))) {
            for (User user : userRepository.getAllUsers()) {
                outputStream.writeObject(user);
            }
        } catch (IOException e) {
            throw new UserSerializationException("Cannot serialize users", e);
        }
    }

    @Override
    public void deserializeRepository(UserRepository userRepository) {
        Validator.validateNullObject(Field.USER_REPOSITORY, userRepository);
        createDeserializationFile();

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(USERS_FILE))) {
            User user;
            while ((user = (User) inputStream.readObject()) != null) {
                userRepository.addUser(user);
            }
        } catch (EOFException eofException) {
            return;
        } catch (IOException | ClassNotFoundException e) {
            throw new UserDeserializationException("Cannot deserialize users", e);
        }
    }

    private static void createDeserializationFile() {
        try {
            if (Files.notExists(USERS_DIRECTORY)) {
                Files.createDirectory(USERS_DIRECTORY);
            }
            if (Files.notExists(USERS_FILE)) {
                Files.createFile(USERS_FILE);
            }
        } catch (IOException e) {
            throw new UserDeserializationException("Cannot create a new file", e);
        }
    }
}
