package P2PSystem;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;

public class PeerClient {
    private static final int DEFAULT_SERVER_PORT = 7734;
    private final String SERVER_HOSTNAME;
    private final int SERVER_PORT;
    private final int UPLOAD_LOCAL_PORT;
    private String LOCAL_HOSTNAME = null;
    private OutputStream out = null;
    private boolean isStopped = false;
    private Server peerServer = null;

    public PeerClient(String serverHostname, int uploadPort) {
        this(serverHostname, DEFAULT_SERVER_PORT, uploadPort);
    }

    public PeerClient(String serverHostname, int serverPort, int uploadPort) {
        this.SERVER_HOSTNAME = serverHostname;
        this.SERVER_PORT = serverPort;
        this.UPLOAD_LOCAL_PORT = uploadPort;
    }

    public void start() throws RuntimeException {
        this.isStopped = false;
        peerServer = new Server(UPLOAD_LOCAL_PORT, Server.Type.PEER_SERVER);
        new Thread(peerServer).start();                           //  running peer server on another thread
        try {
            Socket socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
            out = socket.getOutputStream();
            LOCAL_HOSTNAME = socket.getLocalAddress().getHostName();
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to server at " + SERVER_HOSTNAME + " on port " + SERVER_PORT);
        }
    }

    public void stop() {
        isStopped = true;
    }

    public boolean addRFC(RFC rfc) {
        PeerServerData peerServerData = peerServer.getPeerServerData();
        if (peerServerData == null) return false;
        peerServerData.addRFC(rfc);
        if (!uploadRFC(rfc)) {
            peerServerData.removeRFC(rfc);
            return false;
        }
        return true;
    }

    public boolean uploadRFC(RFC rfc) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", LOCAL_HOSTNAME);
        headers.put("Port", String.valueOf(UPLOAD_LOCAL_PORT));
        headers.put("Title", rfc.getTitle());
        try {
            MessageGenerator.generateRequest(out, MessageGenerator.Method.ADD, rfc.getRFCNum(), headers);
            return true;
        } catch (MessageFormatException | IOException e) {
            System.out.println("Error uploading RFC: " + rfc);
            return false;
        }
    }

    public boolean listRFCs() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", LOCAL_HOSTNAME);
        headers.put("Port", String.valueOf(UPLOAD_LOCAL_PORT));
        try {
            MessageGenerator.generateRequest(out, MessageGenerator.Method.LIST, headers);
            return true;
        } catch (MessageFormatException | IOException e) {
            System.out.println("Error listing all RFCs");
            return false;
        }
    }

    public boolean lookupRFC(int RFCNum, String title) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", LOCAL_HOSTNAME);
        headers.put("Port", String.valueOf(UPLOAD_LOCAL_PORT));
        headers.put("Title", title);
        try {
            MessageGenerator.generateRequest(out, MessageGenerator.Method.LOOKUP, RFCNum, headers);
            return true;
        } catch (MessageFormatException | IOException e) {
            System.out.println("Error in lookup RFC " + RFCNum + " with title " + title);
            return false;
        }
    }
}
