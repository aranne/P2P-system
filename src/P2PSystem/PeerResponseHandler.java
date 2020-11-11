package P2PSystem;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PeerResponseHandler implements Runnable {

    private final Socket socket;
    private final Path file;
    private final String PEER_HOSTNAME;

    public PeerResponseHandler(Socket socket, Path file) {
        this.socket = socket;
        this.file = file;
        PEER_HOSTNAME = socket.getInetAddress().getHostName();
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        String line;
        try (InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()).length() != 0) {
                sb.append(line).append("\r\n");
            }
            System.out.println("Get response from peer: \n");
            System.out.println(sb.toString());
            try {
                Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Error reading from the input stream or writing to RFC file: " + e.getMessage());
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error receiving response from peer: " + e.getMessage());
        } finally {
            try {
                socket.close();                                 // closing socket to peer
                System.out.println("Connection to peer " + PEER_HOSTNAME + " is closed");
            } catch (IOException e) {
                System.out.println("Error closing socket to peer");
            }
        }
    }
}
