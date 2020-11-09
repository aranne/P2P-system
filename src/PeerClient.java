import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class PeerClient {
    private int SERVER_PORT = 7734;
    private int LOCAL_PORT;
    private final String SERVER_HOSTNAME;
    private String LOCAL_HOSTNAME;
    private Socket socket = null;
    private OutputStream out = null;
    private boolean isClosed = false;
    private List<RFC> RFCList = new ArrayList<>();

    public PeerClient(String serverHostname) {
        this.SERVER_HOSTNAME = serverHostname;
    }

    public PeerClient(String serverHostname, int serverPort) {
        this.SERVER_HOSTNAME = serverHostname;
        this.SERVER_PORT = serverPort;
    }

    public void start() {
        try {
            socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
            out = socket.getOutputStream();
            LOCAL_PORT = socket.getLocalPort();
            LOCAL_HOSTNAME = socket.getLocalAddress().getHostName();
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to server at " + SERVER_HOSTNAME + " on port " + SERVER_PORT);
        }
    }

    public void addRFC(RFC rfc) {
        RFCList.add(rfc);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", LOCAL_HOSTNAME);
        headers.put("Port", String.valueOf(LOCAL_PORT));
        headers.put("Title", rfc.getTitle());
        try {
            MessageGenerator.generateRequest(out, MessageGenerator.Method.ADD, rfc.getRFCNum(), headers);
        } catch (MessageFormatException | IOException e) {
            System.out.println("Error in adding rfc: " + rfc);
            RFCList.remove(rfc);
        }
    }
}
