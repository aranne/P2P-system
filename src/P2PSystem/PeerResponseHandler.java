package P2PSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PeerResponseHandler implements Runnable {

    private InputStream in;

    public PeerResponseHandler(InputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()).length() != 0) {
                sb.append(line).append("\r\n");
            }
            System.out.println("Get response from peer: \n");
            System.out.println(sb.toString());


        } catch (IOException e) {
            System.out.println("Error receiving response from peer");
        }
    }
}
