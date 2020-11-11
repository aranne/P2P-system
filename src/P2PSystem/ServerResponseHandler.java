package P2PSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerResponseHandler implements Runnable {

    Socket socket;

    public ServerResponseHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        String line;
        try (InputStream in = socket.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) { // end of stream
                sb.append(line).append("\r\n");
                if (line.length() == 0) {
                    System.out.println("Get response from central server: \n");
                    System.out.println(sb.toString());
                    sb.setLength(0);
                }
            }
        } catch (IOException e) {
            if (socket.isClosed()) {
                System.out.println("Socket to central server is closed");
            } else {
                System.out.println("Error receiving response from central server");
            }
        }
    }
}
