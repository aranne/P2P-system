package P2PSystem;

import Pages.ClientPage.ClientPage;

public class P2PSystemClient {
    private static PeerClient peerClient = null;

    public static void setPeerClient(PeerClient peerClient) {
        P2PSystemClient.peerClient = peerClient;
    }

    public static PeerClient getPeerClient() {
        return peerClient;
    }

    public static void main(String[] args) {
        new ClientPage().display();
    }
}
