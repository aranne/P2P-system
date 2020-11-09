import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerData {

    private final List<PeerInfo> peerInfos = new ArrayList<>();
    private final List<RFCIndex> rfcIndices = new ArrayList<>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public List<PeerInfo> getPeerInfos() {
        return peerInfos;
    }

    public List<RFCIndex> getRfcIndices() {
        return rfcIndices;
    }

    public void addPeerInfo(PeerInfo peerInfo)  {
        w.lock();
        try {
            peerInfos.add(peerInfo);
        } finally {
            w.unlock();
        }
    }

    public void removePeerInfo(PeerInfo peerInfo) {
        w.lock();
        try {
            peerInfos.removeIf(peer -> peer.getHostname().equals(peerInfo.getHostname()));
        } finally {
            w.unlock();
        }
    }

    public void addRFCIndex(RFCIndex rfcIndex) {
        w.lock();
        try {
            rfcIndices.add(rfcIndex);
        } finally {
            w.unlock();
        }
    }

    public void removeRFCIndices(PeerInfo peerInfo) {
        w.lock();
        try {
            rfcIndices.removeIf(rfc -> rfc.getHostname().equals(peerInfo.getHostname()));
        } finally {
            w.unlock();
        }
    }

    @Override
    public String toString() {
        return "ServerData{" +
                "peerInfos=" + peerInfos +
                ", rfcIndices=" + rfcIndices +
                '}';
    }

    /* PeerInfo class and RFCIndex class */

    public static class PeerInfo {
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
            return "ServerData.PeerInfo{" +
                    "hostname='" + hostname + '\'' +
                    ", listeningPort=" + listeningPort +
                    '}';
        }
    }

    public static class RFCIndex {
        private int RFCNum;
        private String title;
        private String hostname;

        public RFCIndex(int RFCNum, String title, String hostname) {
            this.RFCNum = RFCNum;
            this.title = title;
            this.hostname = hostname;
        }

        public int getRFCNum() {
            return RFCNum;
        }

        public String getTitle() {
            return title;
        }

        public String getHostname() {
            return hostname;
        }

        @Override
        public String toString() {
            return "ServerData.RFCIndex{" +
                    "RFCNum=" + RFCNum +
                    ", title='" + title + '\'' +
                    ", hostname='" + hostname + '\'' +
                    '}';
        }
    }
}
