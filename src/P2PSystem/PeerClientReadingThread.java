package P2PSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PeerClientReadingThread implements Runnable {

    InputStream in;

    public PeerClientReadingThread(InputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) { // end of stream
                sb.append(line).append("\r\n");
                if (line.length() == 0) {
                    System.out.println("Get response from central server: \n");
                    System.out.println(sb.toString());
                    sb.setLength(0);
                }
            }
            in.close();
            System.out.println("Connection to central server is closed");
        } catch (IOException e) {
            System.out.println("Error receiving response from central server");
        }

    }
}
