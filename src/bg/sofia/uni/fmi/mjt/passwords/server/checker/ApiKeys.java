package bg.sofia.uni.fmi.mjt.passwords.server.checker;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import bg.sofia.uni.fmi.mjt.passwords.server.util.Field;
import bg.sofia.uni.fmi.mjt.passwords.server.util.Validator;
import com.google.gson.Gson;

public record ApiKeys(String key, String secret) {
  private static final Gson GSON = new Gson();

  public static ApiKeys of(Path path) {
    Validator.validateNullObject(Field.PATH, path);

    try (FileReader reader = new FileReader(path.toFile())) {
      return of(reader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static ApiKeys of(Reader reader) {
    Validator.validateNullObject(Field.READER, reader);

    return GSON.fromJson(reader, ApiKeys.class);
  }
}
