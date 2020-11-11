package P2PSystem;

public class TestServer {
    public static void main(String[] args) {
        Server server = new Server(Server.Type.CENTRAL_SERVER);
        server.run();
    }
}
