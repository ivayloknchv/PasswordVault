package bg.sofia.uni.fmi.mjt.passwords.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bg.sofia.uni.fmi.mjt.passwords.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.passwords.server.command.ServerMessage;
import bg.sofia.uni.fmi.mjt.passwords.server.log.Logger;
import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.InMemoryUserRepository;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.adapter.UsersBinaryFileAdapter;
import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.adapter.UsersFileAdapter;

public class PasswordVaultServer {
  private static final int SERVER_PORT = 6666;
  private static final int BUFFER_SIZE = 1024;
  private static final String HOST = "localhost";

  private static final String SERVER_START = "Server started.";
  private static final String SERVER_STOP = "Server is stopped. Data is persisted.";

  private static final Logger LOGGER = Logger.getInstance();

  private boolean isServerWorking;
  private Selector selector;
  private final int port;

  private final UserRepository userRepository;
  private final Map<String, User> activeUsers;
  private final UsersFileAdapter fileAdapter;
  private final CommandExecutor commandExecutor;

  public PasswordVaultServer(int port) {
    this.port = port;
    this.userRepository = InMemoryUserRepository.getInstance();
    this.activeUsers = new ConcurrentHashMap<>();
    this.fileAdapter = new UsersBinaryFileAdapter();
    this.commandExecutor = new CommandExecutor(userRepository, activeUsers);
  }

  public void start() {
    try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
      fileAdapter.deserializeRepository(userRepository);
      selector = Selector.open();

      configureServerSocketChannel(serverSocketChannel, selector);
      isServerWorking = true;

      while (isServerWorking) {
        handleClientRequests();
      }
    } catch (IOException ioException) {
      stop();
      LOGGER.log(ioException);
      throw new UncheckedIOException("Failed to start server", ioException);
    }
  }

  private void handleClientRequests() {
    try {
      int readyChannels = selector.select();
      if (readyChannels == 0) {
        return;
      }

      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        keyIterator.remove();

        if (key.isReadable()) {
          handleReadableKey(key);
        } else if (key.isAcceptable()) {
          accept(selector, key);
        }
      }
    } catch (IOException ioException) {
      LOGGER.log(ioException);
      System.out.println("Error occurred while processing client request: " + ioException.getMessage());
    }
  }

  private void handleReadableKey(SelectionKey key) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel();
    ClientSession session = (ClientSession) (key.attachment());
    ByteBuffer buffer = session.getBuffer();

    String clientInput = getClientInput(clientChannel, buffer);
    if (clientInput == null) {
      return;
    }

    String output = commandExecutor.execute(clientInput, key);
    writeClientOutput(clientChannel, output, buffer);

    if (output.equals(ServerMessage.DISCONNECTED.text())) {
      key.cancel();
      clientChannel.close();
    }
  }

  private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
    channel.bind(new InetSocketAddress(HOST, this.port));
    channel.configureBlocking(false);
    channel.register(selector, SelectionKey.OP_ACCEPT);
  }

  private String getClientInput(SocketChannel clientChannel, ByteBuffer buffer) throws IOException {
    buffer.clear();

    int readBytes = clientChannel.read(buffer);
    if (readBytes < 0) {
      clientChannel.close();
      return null;
    }

    buffer.flip();

    byte[] clientInputBytes = new byte[buffer.remaining()];
    buffer.get(clientInputBytes);

    return new String(clientInputBytes, StandardCharsets.UTF_8).trim();
  }

  private void writeClientOutput(SocketChannel clientChannel, String output, ByteBuffer buffer) throws IOException {
    buffer.clear();
    buffer.put((output + System.lineSeparator()).getBytes());
    buffer.flip();

    clientChannel.write(buffer);
  }

  private void accept(Selector selector, SelectionKey key) throws IOException {
    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
    SocketChannel clientChannel = serverChannel.accept();

    if (clientChannel == null) {
      return;
    }

    clientChannel.configureBlocking(false);
    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    clientChannel.register(selector, SelectionKey.OP_READ, ClientSession.start(buffer));
  }

  public void stop() {
    try {
      this.isServerWorking = false;
      if (selector != null && selector.isOpen()) {
        selector.wakeup();
      }
      fileAdapter.serializeRepository(userRepository);
      activeUsers.clear();
    } catch (Exception e) {
      LOGGER.log(e);
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    PasswordVaultServer server = new PasswordVaultServer(SERVER_PORT);

    Thread runningServerThread = new Thread(() -> {
      LOGGER.log(SERVER_START);
      System.out.println(SERVER_START);
      server.start();
    });
    runningServerThread.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      LOGGER.log(SERVER_STOP);
      System.out.println(SERVER_STOP);
      server.stop();
    }));
  }
}
