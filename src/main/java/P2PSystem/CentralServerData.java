package P2PSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CentralServerData {

    private final List<PeerInfo> peerInfos = new ArrayList<>();
    private final List<RFCIndex> RFCIndices = new ArrayList<>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public void addPeerInfo(PeerInfo peerInfo)  {
        w.lock();
        try {
            boolean find = false;
            for (PeerInfo peer : peerInfos) {
                if (peer.hostname.equals(peerInfo.hostname) && peer.getUploadPort() == peerInfo.getUploadPort()) {
                    find = true;
                    break;
                }
            }
            if (!find) peerInfos.add(peerInfo);
        } finally {
            w.unlock();
        }
    }

    public void removePeerInfo(PeerInfo peerInfo) {
        w.lock();
        try {
            peerInfos.removeIf(peer -> peer.getHostname().equals(peerInfo.getHostname()) && peer.getUploadPort() == peerInfo.getUploadPort());
        } finally {
            w.unlock();
        }
    }

    public PeerInfo getPeerInfo(String hostname) {
        r.lock();
        try {
            for (PeerInfo peer : peerInfos) {
                if (peer.getHostname().equals(hostname)) return peer;
            }
            return null;
        } finally {
            r.unlock();
        }
    }

    public void addRFCIndex(RFCIndex rfcIndex) {
        w.lock();
        try {
            RFCIndices.add(rfcIndex);
        } finally {
            w.unlock();
        }
    }

    public void removeRFCIndices(PeerInfo peerInfo) {
        w.lock();
        try {
            RFCIndices.removeIf(rfc -> rfc.getHostname().equals(peerInfo.getHostname()));
        } finally {
            w.unlock();
        }
    }

    public List<RFCIndex> lookupRFCIndices(RFC rfc) {
        List<RFCIndex> res = new ArrayList<>();
        r.lock();
        try {
            for (RFCIndex rfcIndex : RFCIndices) {
                if (rfcIndex.RFCNum == rfc.getRFCNum() && rfcIndex.title.equals(rfc.getTitle())) {
                    res.add(rfcIndex);
                }
            }
            return res;
        } finally {
            r.unlock();
        }
    }

    public List<RFCIndex> listRFCIndices() {
        r.lock();
        try {
            return RFCIndices;
        } finally {
            r.unlock();
        }
    }

    @Override
    public String toString() {
        return "ServerData{" +
                "peerInfos=" + peerInfos +
                ", rfcIndices=" + RFCIndices +
                '}';
    }

    /* PeerInfo class and RFCIndex class */

    public static class PeerInfo {
        private String hostname;
        private int uploadPort;

        public PeerInfo(String hostname, int uploadPort) {
            this.hostname = hostname;
            this.uploadPort = uploadPort;
        }

        public String getHostname() {
            return hostname;
        }

        public int getUploadPort() {
            return uploadPort;
        }

        @Override
        public String toString() {
            return "ServerData.PeerInfo{" +
                    "hostname='" + hostname + '\'' +
                    ", listeningPort=" + uploadPort +
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
