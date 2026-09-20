package bg.sofia.uni.fmi.mjt.passwords.server.checker.client;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface EnzoicClient {

  HttpResponse<String> fetchPasswordsHashes(String plainPassword) throws IOException, InterruptedException;
}
