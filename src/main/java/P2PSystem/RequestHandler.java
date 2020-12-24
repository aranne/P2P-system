package P2PSystem;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RequestHandler implements Runnable {

    private final String LOCAL_RFC_DIR = "./localRFCs/";
    private final Socket socket;
    private final Server.Type type;
    private CentralServerData centralServerData = null;
    private PeerServerData peerServerData = null;
    private final String LOCAL_HOSTNAME;
    private CentralServerData.PeerInfo currentPeer = null;
    private static final String VERSION = "P2P-CI/1.0";
    private static final String[] newMsg = {"Get new message from client ", "Get new message from peer "};
    private static final String[] errorMsg = {"Error receiving request in central server", "Error receiving request in peer server"};

    public RequestHandler(Socket clientSocket, CentralServerData centralServerData) {
        this.type = Server.Type.CENTRAL_SERVER;
        this.socket = clientSocket;
        this.centralServerData = centralServerData;
        this.LOCAL_HOSTNAME = clientSocket.getLocalAddress().getHostName();
    }

    public RequestHandler(Socket clientSocket, PeerServerData peerServerData) {
        this.type = Server.Type.PEER_SERVER;
        this.socket = clientSocket;
        this.peerServerData = peerServerData;
        this.LOCAL_HOSTNAME = clientSocket.getLocalAddress().getHostName();
    }

    @Override
    public void run() {
        MessageParser parser = new MessageParser();
        StringBuilder sb = new StringBuilder();
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) { // end of stream
                sb.append(line).append("\r\n");
                if (line.length() == 0) {            // end of a single request
                    System.out.println(newMsg[type.getValue()] + LOCAL_HOSTNAME + ": \n");
                    MessageParser.Message message = parser.parseRequest(sb.toString());
                    handleMessage(message, out);
                    sb.setLength(0);
                    if (type == Server.Type.PEER_SERVER) break;  // only receives one request and closing socket
                }
            }
            socket.close();
            System.out.println("Socket is closed with " + LOCAL_HOSTNAME);
        } catch (IOException | MessageFormatException e) {
            System.out.println(errorMsg[type.getValue()] + " :" + e.getMessage());
        } finally {
            if (type == Server.Type.CENTRAL_SERVER) {
                if (currentPeer != null) {
                    centralServerData.removePeerInfo(currentPeer);
                    centralServerData.removeRFCIndices(currentPeer);
                }
                System.out.println("After leaving "+ centralServerData);
            }
        }
    }

    private void handleMessage(MessageParser.Message message, OutputStream out) {
        String[] requestLine = message.requestLine.split(" ");
        if (requestLine.length != 4 && requestLine.length != 3) {
            handleBadRequest(out);
            return;
        }

        MessageGenerator.Method method;
        try {
            method = Enum.valueOf(MessageGenerator.Method.class, requestLine[0]);
        } catch (IllegalArgumentException e) {
            handleBadRequest(out);
            return;
        }

        if (requestLine.length == 4 && method == MessageGenerator.Method.LIST ||
            requestLine.length == 3 && method != MessageGenerator.Method.LIST) {
            handleBadRequest(out);
            return;
        }

        if (method == MessageGenerator.Method.GET && type != Server.Type.PEER_SERVER ||
            method != MessageGenerator.Method.GET && type == Server.Type.PEER_SERVER) {
            handleBadRequest(out);
            return;
        }

        int RFCNum = -1;
        if (method != MessageGenerator.Method.LIST) {
            try {
                RFCNum = Integer.parseInt(requestLine[2]);
            } catch (NumberFormatException e) {
                handleBadRequest(out);
                return;
            }
        }

        String P2PVersion = method == MessageGenerator.Method.LIST ? requestLine[2] : requestLine[3];
        if (!P2PVersion.equals(VERSION)) {
            handleNotSupportedVersion(out);
            return;
        }

        LinkedHashMap<String, String> headers = message.headers;
        String hostName = headers.get("Host");
        if (hostName == null) {
            handleBadRequest(out);
            return;
        }

        String port = headers.get("Port");
        int uploadPort = -1;
        if (port == null && method != MessageGenerator.Method.GET) {
            handleBadRequest(out);
            return;
        } else if (port != null) {
            try {
                uploadPort = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                handleBadRequest(out);
                return;
            }
        }

        String title = headers.get("Title");
        if (title == null && (method == MessageGenerator.Method.ADD || method == MessageGenerator.Method.LOOKUP)) {
            handleBadRequest(out);
            return;
        }

        String OS = headers.get("OS");
        if (OS == null && method == MessageGenerator.Method.GET) {
            handleBadRequest(out);
            return;
        }
        switch (method) {
            case ADD:
                currentPeer = new CentralServerData.PeerInfo(hostName, uploadPort);
                CentralServerData.RFCIndex rfcIndex = new CentralServerData.RFCIndex(RFCNum, title, hostName);
                handleAdd(currentPeer, rfcIndex, out);
                break;
            case LOOKUP:
                RFC rfc = new RFC(RFCNum, title);
                handleLookUp(rfc, out);
                break;
            case LIST:
                handleList(out);
                break;
            case GET:
                handleGet(RFCNum, out);
                break;
        }
    }

    private void handleBadRequest(OutputStream out) {
        List<String> RFCRecords = new ArrayList<>();
        try {
            MessageGenerator.generateResponse(out, 400, "Bad Request", RFCRecords);
        } catch (IOException e) {
            System.out.println("Error sending response to client");
        }
    }

    private void handleNotSupportedVersion(OutputStream out) {
        List<String> RFCRecords = new ArrayList<>();
        try {
            MessageGenerator.generateResponse(out, 505, "P2P-CI Version Not Supported", RFCRecords);
        } catch (IOException e) {
            System.out.println("Error sending response to client");
        }
    }

    private void handleAdd(CentralServerData.PeerInfo currentPeer, CentralServerData.RFCIndex rfcIndex, OutputStream out) {
        centralServerData.addPeerInfo(currentPeer);
        centralServerData.addRFCIndex(rfcIndex);
        System.out.println("Server data updated: "+ centralServerData);

        List<String> RFCRecords = new ArrayList<>();
        String RFCRecord = MessageGenerator.generateRFCResponseFormat(rfcIndex.getRFCNum(), rfcIndex.getTitle(), rfcIndex.getHostname(), currentPeer.getUploadPort());
        RFCRecords.add(RFCRecord);
        try {
            MessageGenerator.generateResponse(out, 200, "OK", RFCRecords);
        } catch (IOException e) {
            System.out.println("Error sending response to Add request from " + currentPeer.getHostname());
        }
    }

    private void handleLookUp(RFC rfc, OutputStream out) {
        List<CentralServerData.RFCIndex> RFCIndices = centralServerData.lookupRFCIndices(rfc);
        List<String> RFCRecords = new ArrayList<>();
        for  (CentralServerData.RFCIndex  rfcIndex : RFCIndices) {
            CentralServerData.PeerInfo uploadPeer = centralServerData.getPeerInfo(rfcIndex.getHostname());
            int uploadPort = uploadPeer.getUploadPort();
            String RFCRecord = MessageGenerator.generateRFCResponseFormat(rfcIndex.getRFCNum(), rfcIndex.getTitle(), rfcIndex.getHostname(), uploadPort);
            RFCRecords.add(RFCRecord);
        }
        try {
            if (!RFCRecords.isEmpty()) {
                MessageGenerator.generateResponse(out, 200, "OK", RFCRecords);
            } else {
                MessageGenerator.generateResponse(out, 404, "Not Found", RFCRecords);
            }
        } catch (IOException e) {
            System.out.println("Error sending response to LookUp request from " + currentPeer.getHostname());
        }
    }

    private void handleList(OutputStream out) {
        List<CentralServerData.RFCIndex> RFCIndices = centralServerData.listRFCIndices();
        List<String> RFCRecords = new ArrayList<>();
        for  (CentralServerData.RFCIndex  rfcIndex : RFCIndices) {
            CentralServerData.PeerInfo uploadPeer = centralServerData.getPeerInfo(rfcIndex.getHostname());
            int uploadPort = uploadPeer.getUploadPort();
            String RFCRecord = MessageGenerator.generateRFCResponseFormat(rfcIndex.getRFCNum(), rfcIndex.getTitle(), rfcIndex.getHostname(), uploadPort);
            RFCRecords.add(RFCRecord);
        }
        try {
            MessageGenerator.generateResponse(out, 200, "OK", RFCRecords);
        } catch (IOException e) {
            System.out.println("Error sending response to List request from " + currentPeer.getHostname());
        }
    }

    private void handleGet(int RFCNum, OutputStream out) {
        Path file = Paths.get(LOCAL_RFC_DIR + "rfc" + RFCNum + ".txt");
        try {
            BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
            LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            headers.put("Date", LocalDateTime.now().toString());
            headers.put("OS", System.getProperty("os.name"));
            headers.put("Last-Modified", attributes.lastModifiedTime().toString());
            headers.put("Content-Length", String.valueOf(attributes.size()));
            headers.put("Content-Type", "text/text");
            try {
                MessageGenerator.generateResponse(out, 200, "OK", headers, file);
            } catch (IOException e) {
                System.out.println("Error sending RFC " + RFCNum + " :" + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Cannot access RFC " + RFCNum);
        }
    }
}
