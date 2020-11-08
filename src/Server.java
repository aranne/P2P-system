import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final int LISTENING_PORT = 7734;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;
    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor executor;
    private boolean isStopped = false;
    private final ServerData serverData = new ServerData();

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(LISTENING_PORT);
        this.executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void run() {
        while (!isStopped) {
            try {
                Socket socket = serverSocket.accept();
                executor.execute(new ServerHandleRequestThread(socket, serverData));
            } catch (IOException e) {
                System.out.println("I/O error " + e);
            }
        }
    }

    public void stop() throws IOException {
        isStopped = true;
        serverSocket.close();
        System.out.println("Socket closed");
        executor.shutdown();
        while (!executor.isTerminated()) {}
        System.out.println("Finished all threads");
    }
}
