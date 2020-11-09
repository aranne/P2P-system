package P2PSystem;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
//        ServerData serverData = new ServerData();
//        serverData.addPeerInfo(new ServerData.PeerInfo("AA", 11));
//        serverData.addPeerInfo(new ServerData.PeerInfo("BB", 22));
//        serverData.addPeerInfo(new ServerData.PeerInfo("CC",  33));
//        serverData.removePeerInfo(new ServerData.PeerInfo("BB", 22));
//        System.out.println(serverData.getPeerInfos());
//
        MessageParser parser = new MessageParser();
        Map<String, String> headers = new HashMap<>();
//        try {
//            headers.put("Host", "thishost.csc.ncsu.edu");
//            headers.put("Port", String.valueOf(8080));
//            headers.put("Title", "A Proferred Official ICP");
//            ProtocolGenerator.generateRequest(System.out, ProtocolGenerator.Method.ADD, 100, headers);
//        } catch (Exception e) {
//            System.out.println("ERROR " + e.getMessage());
//        }
//
//        try {
//            Path file = Paths.get("RFCs/rfc119.txt");
//            BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
//            headers.clear();
//            headers.put("Date", LocalDateTime.now().toString());
//            headers.put("OS", System.getProperty("os.name"));
//            headers.put("Last-Modified", attr.lastModifiedTime().toString());
//            headers.put("Content-Length", String.valueOf(attr.size()));
//            headers.put("Content-Type", "text/text");
//            ProtocolGenerator.generateResponse(System.out, 200, "OK", headers, file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
