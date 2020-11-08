import java.net.Socket;

public class ServerHandleRequestThread implements Runnable {

    private Socket socket;

    public ServerHandleRequestThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
