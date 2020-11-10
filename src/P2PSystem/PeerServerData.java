package P2PSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PeerServerData {

    private final List<RFC> RFCList = new ArrayList<>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public void addRFC(RFC rfc) {
        w.lock();
        try {
            RFCList.add(rfc);
        } finally {
            w.unlock();
        }
    }

    public RFC getRFC(int RFCNum) {
        r.lock();
        try {
            for (RFC rfc : RFCList) {
                if (rfc.getRFCNum() == RFCNum) return rfc;
            }
            return null;
        } finally {
            r.unlock();
        }
    }

    public void removeRFC(RFC rfc) {
        w.lock();
        try {
            RFCList.removeIf(RFC -> RFC.getRFCNum() == rfc.getRFCNum() && RFC.getTitle().equals(rfc.getTitle()));
        } finally {
            w.unlock();
        }
    }

    public List<RFC> getAllRFCs() {
        r.lock();
        try {
            return RFCList;
        } finally {
            r.unlock();
        }
    }
}
