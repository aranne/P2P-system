package P2PSystem;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final CentralServerData centralServerData;
    private final String LOCAL_HOSTNAME;
    private CentralServerData.PeerInfo currentPeer = null;

    public ClientHandler(Socket clientSocket, CentralServerData centralServerData) {
        this.socket = clientSocket;
        this.centralServerData = centralServerData;
        this.LOCAL_HOSTNAME = clientSocket.getLocalAddress().getHostName();
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
                    handleMessage(message);
                    sb.setLength(0);
                }
            }
            in.close();
            socket.close();
            System.out.println("Socket is closed with: " + LOCAL_HOSTNAME);
        } catch (IOException | MessageFormatException e) {
            System.out.println("IO Error in server thread");
        } finally {
            if (currentPeer != null) {
                centralServerData.removePeerInfo(currentPeer);
                centralServerData.removeRFCIndices(currentPeer);
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
                CentralServerData.RFCIndex rfcIndex = new CentralServerData.RFCIndex(RFCNum, title, hostName);
                currentPeer = new CentralServerData.PeerInfo(hostName, uploadPort);
                handleAdd(currentPeer, rfcIndex);
                break;
            case LOOKUP:
                break;
            case LIST:

        }
    }

    private void handleAdd(CentralServerData.PeerInfo currentPeer, CentralServerData.RFCIndex rfcIndex) {
        centralServerData.addPeerInfo(currentPeer);
        centralServerData.addRFCIndex(rfcIndex);
        System.out.println("DEBUG "+ centralServerData);
    }
}
