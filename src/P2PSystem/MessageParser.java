package P2PSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;

/*
    Peer to peer/Peer to server protocol parser

    Protocol format:
    Request-Line <cr> <lf>
    header field name: <sp> value <cr> <lf>
    header field name: <sp> value <cr> <lf>...
    <cr> <lf>
    Message-Body
    <cr> <lf>
 */
public class MessageParser {
    public static class Message {
        String requestLine;
        LinkedHashMap<String, String> headers;
        StringBuilder messageBody;
        public Message() {
            headers = new LinkedHashMap<>();
            messageBody = new StringBuilder();
        }
    }

    public Message parseRequest(String request) throws MessageFormatException, IOException {
        System.out.println(request);
        BufferedReader br = new BufferedReader(new StringReader(request));
        Message message = new Message();

        setRequestLine(br.readLine(), message);        // Request-Line

        String header = br.readLine();
        while (header.length() > 0) {                  // reads until \r\n
            appendHeader(header, message);
            header = br.readLine();
        }

        String bodyLine = br.readLine();
        while (bodyLine != null) {                     // reads until end of stream
            appendMessageBody(bodyLine, message);      // if reads \r\n, bodyLine will be empty string, this will append \r\n
            bodyLine = br.readLine();
        }
        return message;
    }

    private void setRequestLine(String requestLine, Message message) throws MessageFormatException {
        if (requestLine == null || requestLine.length() == 0) throw new MessageFormatException("Invalid Request-Line: " + requestLine);
        message.requestLine = requestLine;
    }

    private void appendHeader(String header, Message message) throws MessageFormatException {
        int idx = header.indexOf(":");
        if (idx == -1) throw new MessageFormatException("Invalid Header Format: " + header);
        message.headers.put(header.substring(0, idx), header.substring(idx + 2));
    }

    private void appendMessageBody(String bodyLine, Message message) {
        message.messageBody.append(bodyLine).append("\r\n");
    }
}
