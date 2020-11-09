import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

/*
    Peer to peer/Peer to server protocol parser
 */
public class ProtocolParser {
    private String requestLine;
    private HashMap<String, String> headers;
    private StringBuilder messageBody;

    public void parseRequest(String request) throws ProtocolFormatException, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(request));

        headers = new HashMap<>();
        messageBody = new StringBuilder();

        setRequestLine(reader.readLine());    // Request-Line

        String header = reader.readLine();
        while (header.length() > 0) {         // reads until \r\n
            appendHeader(header);
            header = reader.readLine();
        }

        String bodyLine = reader.readLine();
        while (bodyLine != null) {            // reads until EOF
            appendMessageBody(bodyLine);      // if reads \r\n, bodyLine will be empty string, this will append \r\n
            bodyLine = reader.readLine();
        }
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getHeaderParameter(String headerName) {
        return headers.get(headerName);
    }

    public String getMessageBody() {
        return messageBody.toString();
    }

    private void setRequestLine(String requestLine) throws ProtocolFormatException {
        if (requestLine == null || requestLine.length() == 0) throw new ProtocolFormatException("Invalid Request-Line: " + requestLine);
        this.requestLine = requestLine;
    }

    private void appendHeader(String header) throws ProtocolFormatException {
        int idx = header.indexOf(":");
        if (idx == -1) throw new ProtocolFormatException("Invalid Header Format: " + header);
        this.headers.put(header.substring(0, idx), header.substring(idx + 2));
    }

    private void appendMessageBody(String bodyLine) {
        messageBody.append(bodyLine).append("\r\n");
    }
}