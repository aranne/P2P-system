package P2PSystem;

public class P2PSystemServer {
    public static void main(String[] args) {
        Server server = new Server(Server.Type.CENTRAL_SERVER);
        server.run();
    }
}
