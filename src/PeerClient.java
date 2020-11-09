import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class PeerClient {
    private int SERVER_PORT = 7734;
    private final int UPLOAD_LOCAL_PORT;
    private final String SERVER_HOSTNAME;
    private String LOCAL_HOSTNAME;
    private OutputStream out = null;
    private boolean isClosed = false;
    private final List<RFC> RFCList = new ArrayList<>();

    public PeerClient(String serverHostname, int uploadPort) {
        this.SERVER_HOSTNAME = serverHostname;
        this.UPLOAD_LOCAL_PORT = uploadPort;
    }

    public PeerClient(String serverHostname, int serverPort, int uploadPort) {
        this.SERVER_HOSTNAME = serverHostname;
        this.SERVER_PORT = serverPort;
        this.UPLOAD_LOCAL_PORT = uploadPort;
    }

    public void start() {
        try {
            Socket socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
            out = socket.getOutputStream();
            LOCAL_HOSTNAME = socket.getLocalAddress().getHostName();
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to server at " + SERVER_HOSTNAME + " on port " + SERVER_PORT);
        }
    }

    public void addRFC(RFC rfc) {
        RFCList.add(rfc);
        uploadRFC(rfc);
    }

    public void uploadRFC(RFC rfc) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", LOCAL_HOSTNAME);
        headers.put("Port", String.valueOf(UPLOAD_LOCAL_PORT));
        headers.put("Title", rfc.getTitle());
        try {
            MessageGenerator.generateRequest(out, MessageGenerator.Method.ADD, rfc.getRFCNum(), headers);
        } catch (MessageFormatException | IOException e) {
            System.out.println("Error uploading rfc: " + rfc);
            RFCList.remove(rfc);
        }
    }
}
