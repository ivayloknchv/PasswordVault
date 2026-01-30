package bg.sofia.uni.fmi.mjt.passwords.server.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Logger {
    private static final Path DIRECTORY_PATH = Path.of("log");
    private static final String FILE_NAME = "logs.txt";
    private static final Path LOGS_FILE_PATH = Path.of(DIRECTORY_PATH + File.separator + FILE_NAME);

    private static final String MESSAGE_FORMAT = "[%s] %s\n";
    private static final String MESSAGE_FORMAT_STACK_TRACE = "[%s] %s. Stack trace: %s\n";

    private static Logger instance;
    private final Writer writer;

    private Logger() {
        try {
            if (Files.notExists(DIRECTORY_PATH)) {
                Files.createDirectory(DIRECTORY_PATH);
            }
            if (Files.notExists(LOGS_FILE_PATH)) {
                Files.createFile(LOGS_FILE_PATH);
            }

            writer = new FileWriter(LOGS_FILE_PATH.toFile(), true);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public synchronized void log(String logMessage) {
        try {
            String formattedMessage = MESSAGE_FORMAT.formatted(LocalDateTime.now(), logMessage);
            writer.write(formattedMessage);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public synchronized void log(Exception exception) {
        try {
            String stackTrace = Arrays.toString(exception.getStackTrace());
            String formattedMessage =
                MESSAGE_FORMAT_STACK_TRACE.formatted(LocalDateTime.now(), exception.getMessage(), stackTrace);
            writer.write(formattedMessage);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
