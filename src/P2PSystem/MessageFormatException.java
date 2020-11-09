package P2PSystem;

public class MessageFormatException extends Exception {

    public MessageFormatException(String message) {
        super(message);
    }
    public MessageFormatException(String message, Throwable err) {
        super(message, err);
    }
}
