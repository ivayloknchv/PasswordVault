package bg.sofia.uni.fmi.mjt.passwords.server;

import java.nio.ByteBuffer;

import bg.sofia.uni.fmi.mjt.passwords.server.user.model.User;

public class ClientSession {
  private ByteBuffer buffer;
  private User loggedUser;

  public ClientSession(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  public ClientSession(ByteBuffer buffer, User loggedUser) {
    this.buffer = buffer;
    this.loggedUser = loggedUser;
  }

  public ByteBuffer getBuffer() {
    return buffer;
  }

  public void setBuffer(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  public User getLoggedUser() {
    return loggedUser;
  }

  public void setLoggedUser(User loggedUser) {
    this.loggedUser = loggedUser;
  }

  public static ClientSession start(ByteBuffer buffer) {
    return new ClientSession(buffer);
  }
}
