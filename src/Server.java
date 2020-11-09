import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;
    private int serverPort = 7734;
    private boolean isStopped = false;
    private ServerSocket serverSocket = null;
    private ThreadPoolExecutor threadPool = null;
    private ServerData serverData = null;

    public Server() {}

    public Server(int port) {
        this.serverPort = port;
    }

    public void start() {
        initServer();
        while (!isStopped) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New connection established with " + socket.getLocalAddress().getHostName());
                threadPool.execute(new ClientHandler(socket, serverData));
            } catch (IOException e) {
                if (isStopped) {
                    System.out.println("Server stopped");
                    break;
                }
                throw new RuntimeException("Error accepting new connections", e);
            }
        }
        threadPool.shutdown();
        System.out.println("Server stopped");
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void stop() {
        isStopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server socket", e);
        }
        System.out.println("Server socket closed");
    }

    private void initServer() {
        this.isStopped = false;
        this.threadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.serverData = new ServerData();

        try {
            this.serverSocket =  new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
        System.out.println("Server is listening on port " + serverPort);
    }
}
