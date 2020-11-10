package P2PSystem;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final CentralServerData centralServerData;
    private final String LOCAL_HOSTNAME;
    private CentralServerData.PeerInfo currentPeer = null;
    private OutputStream out = null;
    private static final String VERSION = "P2P-CI/1.0";
    private static final int ALL = -1;

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
            out = socket.getOutputStream();
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
            out.close();
            socket.close();
            System.out.println("Socket is closed with " + LOCAL_HOSTNAME);
        } catch (IOException | MessageFormatException e) {
            System.out.println("IO error in central server thread");
        } finally {
            if (currentPeer != null) {
                centralServerData.removePeerInfo(currentPeer);
                centralServerData.removeRFCIndices(currentPeer);
            }
            System.out.println("After leaving "+ centralServerData);
        }
    }

    private void handleMessage(MessageParser.Message message) {
        String[] requestLine = message.requestLine.split(" ");
        if (requestLine.length != 4) {
            handleBadRequest();
            return;
        }
        MessageGenerator.Method method;
        try {
            method = Enum.valueOf(MessageGenerator.Method.class, requestLine[0]);
        } catch (IllegalArgumentException e) {
            handleBadRequest();
            return;
        }
        int RFCNum;
        try {
            RFCNum = Integer.parseInt(requestLine[2]);
        } catch (NumberFormatException e) {
            if (requestLine[2].equals("ALL")) RFCNum = ALL;
            else {
                handleBadRequest();
                return;
            }
        }
        String P2PVersion = requestLine[3];
        if (!P2PVersion.equals(VERSION)) {
            handleNotSupportedVersion();
            return;
        }
        LinkedHashMap<String, String> headers = message.headers;
        String hostName = headers.get("Host");
        if (hostName == null) {
            handleBadRequest();
            return;
        }
        int uploadPort;
        try {
            uploadPort = Integer.parseInt(headers.get("Port"));
        } catch (NumberFormatException e) {
            handleBadRequest();
            return;
        }

        String title = headers.get("Title");
        if (title == null && RFCNum != ALL) {
            handleBadRequest();
            return;
        }
        currentPeer = new CentralServerData.PeerInfo(hostName, uploadPort);
        switch (method) {
            case ADD:
                CentralServerData.RFCIndex rfcIndex = new CentralServerData.RFCIndex(RFCNum, title, hostName);
                handleAdd(rfcIndex);
                break;
            case LOOKUP:
                RFC rfc = new RFC(RFCNum, title);
                handleLookUp(rfc);
                break;
            case LIST:
                handleList();
                break;
        }
    }

    private void handleBadRequest() {
        List<String> RFCRecords = new ArrayList<>();
        try {
            MessageGenerator.generateResponse(out, 400, "Bad Request", RFCRecords);
        } catch (IOException e) {
            System.out.println("Error sending response to client");
        }
    }

    private void handleNotSupportedVersion() {
        List<String> RFCRecords = new ArrayList<>();
        try {
            MessageGenerator.generateResponse(out, 505, "P2P-CI Version Not Supported", RFCRecords);
        } catch (IOException e) {
            System.out.println("Error sending response to client");
        }
    }

    private void handleAdd(CentralServerData.RFCIndex rfcIndex) {
        centralServerData.addPeerInfo(currentPeer);
        centralServerData.addRFCIndex(rfcIndex);
        System.out.println("After adding "+ centralServerData);

        List<String> RFCRecords = new ArrayList<>();
        String RFCRecord = MessageGenerator.generateRFCResponseFormat(rfcIndex.getRFCNum(), rfcIndex.getTitle(), rfcIndex.getHostname(), currentPeer.getUploadPort());
        RFCRecords.add(RFCRecord);
        try {
            MessageGenerator.generateResponse(out, 200, "OK", RFCRecords);
        } catch (IOException e) {
            System.out.println("Error sending response to Add request from " + currentPeer.getHostname());
        }
    }

    private void handleLookUp(RFC rfc) {
        List<CentralServerData.RFCIndex> RFCIndices = centralServerData.lookupRFCIndices(rfc);
        List<String> RFCRecords = new ArrayList<>();
        for  (CentralServerData.RFCIndex  rfcIndex : RFCIndices) {
            String RFCRecord = MessageGenerator.generateRFCResponseFormat(rfcIndex.getRFCNum(), rfcIndex.getTitle(), rfcIndex.getHostname(), currentPeer.getUploadPort());
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

    private void handleList() {
        List<CentralServerData.RFCIndex> RFCIndices = centralServerData.listRFCIndices();
        List<String> RFCRecords = new ArrayList<>();
        for  (CentralServerData.RFCIndex  rfcIndex : RFCIndices) {
            String RFCRecord = MessageGenerator.generateRFCResponseFormat(rfcIndex.getRFCNum(), rfcIndex.getTitle(), rfcIndex.getHostname(), currentPeer.getUploadPort());
            RFCRecords.add(RFCRecord);
        }
        try {
            MessageGenerator.generateResponse(out, 200, "OK", RFCRecords);
        } catch (IOException e) {
            System.out.println("Error sending response to List request from " + currentPeer.getHostname());
        }
    }
}
