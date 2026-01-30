package bg.sofia.uni.fmi.mjt.passwords.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PasswordVaultClient {
    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";
    private static final String DISCONNECT_COMMAND = "disconnect";

    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            while (true) {
                System.out.print("Enter command: ");
                String command = scanner.nextLine();
                writer.println(command);

                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    System.out.println(responseLine);
                    if (!reader.ready()) {
                        break;
                    }
                }

                if (DISCONNECT_COMMAND.equals(command)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }
}
