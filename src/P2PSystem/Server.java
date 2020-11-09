package P2PSystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable {
    enum Type {
        CENTRAL_SERVER(0), PEER_SERVER(1);
        private final int value;
        Type(int value) {
            this.value = value;
        }
        public int getValue() {return value;}
    }

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;
    private static final int DEFAULT_SERVER_PORT = 7734;
    private final int serverPort;
    private final Type serverType;
    private boolean isStopped = false;
    private ServerSocket serverSocket = null;
    private ThreadPoolExecutor threadPool = null;
    private CentralServerData centralServerData = null;
    private PeerServerData peerServerData = null;
    private final String[] establishMsg = {"New server-to-peer connection established with ", "New peer-to-peer connection established with "};
    private final String[] stoppedMsg = {"Central server stopped", "Peer server stopped"};
    private final String[] listeningMsg = {"Central server is listening on port ", "Peer server is listening on port "};

    public Server(Type type) {
        this(DEFAULT_SERVER_PORT, type);
    }

    public Server(int port, Type type) {
        this.serverPort = port;
        this.serverType = type;
    }

    public void run() {
        initServer();
        while (!isStopped) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println(establishMsg[serverType.getValue()] + socket.getLocalAddress().getHostName());
                if (serverType == Type.CENTRAL_SERVER) {
                    threadPool.execute(new ClientHandler(socket, centralServerData));
                } else {
                    threadPool.execute(new PeerHandler(socket, peerServerData));
                }
            } catch (IOException e) {
                if (isStopped) {
                    System.out.println(stoppedMsg[serverType.getValue()]);
                    break;
                }
                throw new RuntimeException("Error accepting new connections", e);
            }
        }
        threadPool.shutdown();
        System.out.println(stoppedMsg[serverType.getValue()]);
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
        System.out.println("P2PSystem.Server socket closed");
    }

    public CentralServerData getCentralServerData() {
        return centralServerData;
    }

    public PeerServerData getPeerServerData() {
        return peerServerData;
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
        if (serverType == Type.CENTRAL_SERVER) {
            this.centralServerData = new CentralServerData();
        } else {
            this.peerServerData = new PeerServerData();
        }

        try {
            this.serverSocket =  new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open server on port " + serverPort, e);
        }
        System.out.println(listeningMsg[serverType.getValue()] + serverPort);
    }
}
