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

    public void addRFCIndex(RFC rfc, PeerInfo peerInfo) {
        w.lock();
        try {
            rfcIndices.add(new RFCIndex(rfc.getRFCNum(), rfc.getTitle(), peerInfo.getHostname()));
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
}
