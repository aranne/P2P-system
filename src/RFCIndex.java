import java.util.Objects;

public class RFCIndex {
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
}
