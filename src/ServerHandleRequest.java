import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerHandleRequest implements Runnable {

    private Socket socket;
    private ServerData serverData;
    private ServerData.PeerInfo currentPeer;
    private static final int BUFFER_SIZE = 8192;

    public ServerHandleRequest(Socket socket, ServerData serverData) {
        this.socket = socket;
        this.serverData = serverData;
        this.currentPeer = new ServerData.PeerInfo(socket.getLocalAddress().getHostName(), socket.getLocalPort());
    }

    @Override
    public void run() {
        serverData.addPeerInfo(currentPeer);
        MessageParser parser = new MessageParser();
        StringBuilder sb = new StringBuilder();
        try {
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) { // end of stream
                sb.append(line).append("\r\n");
                if (line.length() == 0) {            // end of a single request
                    System.out.println("Get new message from client " + currentPeer.getHostname() + ": \n");
                    MessageParser.Message message = parser.parseRequest(sb.toString());
                    sb.setLength(0);
                    handleMessage(message);
                }
            }
            in.close();
            socket.close();
        } catch (IOException | MessageFormatException e) {
            System.out.println("IO Error in server thread");
        } finally {
            System.out.println("Socket is closed with: " + currentPeer.getHostname());
            serverData.removePeerInfo(currentPeer);
            serverData.removeRFCIndices(currentPeer);
        }
    }

    private void handleMessage(MessageParser.Message message) {

    }
}
