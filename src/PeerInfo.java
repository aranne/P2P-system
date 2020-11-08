import java.util.Objects;

public class PeerInfo {
    private String hostname;
    private int listeningPort;

    public PeerInfo(String hostname, int listeningPort) {
        this.hostname = hostname;
        this.listeningPort = listeningPort;
    }

    public String getHostname() {
        return hostname;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    @Override
    public String toString() {
        return "PeerInfo{" +
                "hostname='" + hostname + '\'' +
                ", listeningPort=" + listeningPort +
                '}';
    }
}
