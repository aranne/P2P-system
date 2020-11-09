import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ServerData serverData;
    private final String LOCAL_HOSTNAME;
    private ServerData.PeerInfo currentPeer = null;

    public ClientHandler(Socket clientSocket, ServerData serverData) {
        this.socket = clientSocket;
        this.serverData = serverData;
        LOCAL_HOSTNAME = clientSocket.getLocalAddress().getHostName();
    }

    @Override
    public void run() {
        MessageParser parser = new MessageParser();
        StringBuilder sb = new StringBuilder();
        try {
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) { // end of stream
                sb.append(line).append("\r\n");
                if (line.length() == 0) {            // end of a single request
                    System.out.println("Get new message from client " + LOCAL_HOSTNAME + ": \n");
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
            System.out.println("Socket is closed with: " + LOCAL_HOSTNAME);
            if (currentPeer != null) {
                serverData.removePeerInfo(currentPeer);
                serverData.removeRFCIndices(currentPeer);
            }
        }
    }

    private void handleMessage(MessageParser.Message message) {
        String[] requestLine = message.requestLine.split(" ");
        MessageGenerator.Method method = Enum.valueOf(MessageGenerator.Method.class, requestLine[0]);
        LinkedHashMap<String, String> headers = message.headers;
        String hostName = headers.get("Host");
        int uploadPort = Integer.parseInt(headers.get("Port"));
        int RFCNum;
        try {
            RFCNum = Integer.parseInt(requestLine[2]);
        } catch (NumberFormatException e) {
            RFCNum = -1;
        }
        String title = headers.getOrDefault("Title", "");
        switch (method) {
            case ADD:
                ServerData.RFCIndex rfcIndex = new ServerData.RFCIndex(RFCNum, title, hostName);
                currentPeer = new ServerData.PeerInfo(hostName, uploadPort);
                handleAdd(currentPeer, rfcIndex);
                break;
            case LOOKUP:
                break;
            case LIST:

        }
    }

    private void handleAdd(ServerData.PeerInfo currentPeer, ServerData.RFCIndex rfcIndex) {
        serverData.addPeerInfo(currentPeer);
        serverData.addRFCIndex(rfcIndex);
        System.out.println("DEBUG "+serverData);
    }
}
