import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
    Peer to Server Protocol

    Request:
    method <sp> RFC number <sp> version <cr> <lf> header field name <sp> value <cr> <lf>
    header field name <sp> value <cr> <lf>
    <cr> <lf>

    Response:
    version <sp> status code <sp> phrase <cr> <lf>
    <cr> <lf>
    RFC number <sp> RFC title <sp> hostname <sp> upload port number<cr><lf>
    RFC number <sp> RFC title <sp> hostname <sp> upload port number<cr><lf> ...
    <cr> <lf>
 */
public class MessageGenerator {
    enum Method {
        GET,
        ADD,
        LOOKUP,
        LIST
    }
    private static final String VERSION = "P2P-CI/1.0";
    private static final String CRLF = "\r\n";

    public static void generateRequest(OutputStream out, Method method, LinkedHashMap<String, String> headers) throws MessageFormatException, IOException {
        if (method == Method.LIST) {
            generateRequest(out, method, -1, headers);
        } else {
            throw new MessageFormatException("Not supported method: " + method);
        }
    }

    public static void generateRequest(OutputStream out, Method method, int RFCNum, LinkedHashMap<String, String> headers) throws MessageFormatException, IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        if (method == Method.LIST) {
            bw.write(method.toString() + " RFC ALL " + VERSION + CRLF);
        } else if (method == Method.GET || method == Method.ADD || method == Method.LOOKUP) {
            bw.write(method.toString() + " RFC " + RFCNum + " " + VERSION + CRLF);
        } else {
            throw new MessageFormatException("Not supported method: " + method);
        }
        for (String header : headers.keySet()) {
            bw.write(header + ": " + headers.get(header) + CRLF);
        }
        bw.write(CRLF);
        bw.flush();
    }

    public static void generateResponse(OutputStream out, int statusCode, String status, LinkedHashMap<String, String> headers, Path file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        bw.write(VERSION + " " + statusCode + " " + status + CRLF);
        for (String header : headers.keySet()) {
            bw.write(header + ": " + headers.get(header) + CRLF);
        }
        bw.write(CRLF);
        bw.flush();                                          // flush into stream before reading file
        Files.copy(file, out);
        out.flush();
        bw.write(CRLF);
        bw.flush();
    }

    public static void generateResponse(OutputStream out, int statusCode, String status, List<String> RFCRecords) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        bw.write(VERSION + " " + statusCode + " " + status + CRLF);
        bw.write(CRLF);
        for (String RFCRecord : RFCRecords) {
            bw.write(RFCRecord + CRLF);
        }
        bw.write(CRLF);
        bw.flush();
    }
}
