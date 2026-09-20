package bg.sofia.uni.fmi.mjt.passwords.server.user.repository.adapter;

import bg.sofia.uni.fmi.mjt.passwords.server.user.repository.UserRepository;

public interface UsersFileAdapter {

  void serializeRepository(UserRepository userRepository);

  void deserializeRepository(UserRepository userRepository);
}
