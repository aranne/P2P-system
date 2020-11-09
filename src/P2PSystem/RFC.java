package P2PSystem;

public class RFC {
    private int RFCNum;
    private String title;

    public RFC(int RFCNum, String title) {
        this.RFCNum = RFCNum;
        this.title = title;
    }

    public int getRFCNum() {
        return RFCNum;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "P2PSystem.RFC{" +
                "RFCNum=" + RFCNum +
                ", title='" + title + '\'' +
                '}';
    }
}
