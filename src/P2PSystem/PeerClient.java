package P2PSystem;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class PeerClient {
    private static final int DEFAULT_SERVER_PORT = 7734;
    private final String SERVER_HOSTNAME;
    private final int SERVER_PORT;
    private final int UPLOAD_LOCAL_PORT;
    private String LOCAL_HOSTNAME = null;
    private Socket socket = null;
    private OutputStream out = null;
    private Server peerServer = null;

    public PeerClient(String serverHostname, int uploadPort) {
        this(serverHostname, DEFAULT_SERVER_PORT, uploadPort);
    }

    public PeerClient(String serverHostname, int serverPort, int uploadPort) {
        this.SERVER_HOSTNAME = serverHostname;
        this.SERVER_PORT = serverPort;
        this.UPLOAD_LOCAL_PORT = uploadPort;
    }

    public void start() {
        peerServer = new Server(UPLOAD_LOCAL_PORT, Server.Type.PEER_SERVER);
        new Thread(peerServer).start();                           //  running peer server on another thread
        try {
            socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
            out = socket.getOutputStream();
            System.out.println("Connected to server " + socket.getInetAddress().getHostName());
            this.LOCAL_HOSTNAME = socket.getLocalAddress().getHostName();
            new Thread(new ServerResponseHandler(socket)).start();  // running reading thread for response from server
        } catch (IOException e) {
            System.out.println("Cannot connect to server at " + SERVER_HOSTNAME + " on port " + SERVER_PORT + ": " + e.getMessage());
        }
    }

    public void stop() {
        try {
            out.close();
            socket.close();                           // close connection to central server
            System.out.println("Connection to central server is closed");
            peerServer.stop();                        // stop peer server
            System.out.println("Client left");
        } catch (IOException e) {
            System.out.println("Error closing client");
        }
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

//    public boolean uploadAllRFCs() {
//        PeerServerData peerServerData = peerServer.getPeerServerData();
//        if (peerServerData == null) return false;
//        for (RFC rfc : peerServerData.getAllRFCs()) {
//            LinkedHashMap<String, String> headers = new LinkedHashMap<>();
//            headers.put("Host", LOCAL_HOSTNAME);
//            headers.put("Port", String.valueOf(UPLOAD_LOCAL_PORT));
//            headers.put("Title", rfc.getTitle());
//            try {
//                MessageGenerator.generateRequest(out, MessageGenerator.Method.ADD, rfc.getRFCNum(), headers);
//            } catch (MessageFormatException | IOException e) {
//                System.out.println("Error uploading RFCs");
//                return false;
//            }
//        }
//        return true;
//    }

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
            System.out.println("Error lookup RFC " + RFCNum + " with title " + title);
            return false;
        }
    }

    public boolean getRFC(int RFCNum, String hostname, int uploadingPort) {
        try {
            Socket socket = new Socket(hostname, uploadingPort);
            OutputStream out = socket.getOutputStream();
            System.out.println("Connected to client " + hostname + " at port " + uploadingPort);

            Path file = Paths.get("./DownloadRFCs/rfc" + RFCNum + ".txt");
            new Thread(new PeerResponseHandler(socket, file)).start();      // get response from other peer

            LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            headers.put("Host", LOCAL_HOSTNAME);
            headers.put("OS", System.getProperty("os.name"));
            try {
                MessageGenerator.generateRequest(out, MessageGenerator.Method.GET, RFCNum, headers);
            } catch (MessageFormatException e) {
                System.out.println("Error sending request to download RFC from client " + hostname + " at port " + uploadingPort);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error connecting to peer " + hostname + " at port " + uploadingPort);
            return false;
        }
    }
}
