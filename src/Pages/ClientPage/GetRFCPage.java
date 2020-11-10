package Pages.ClientPage;

import P2PSystem.P2PSystemClient;
import Pages.BasePage;
import Pages.PageView;

public class GetRFCPage extends BasePage implements PageView {

    public GetRFCPage() {
        super();
        menu.add("Download");
        menu.add("Go back");
        pageTitle = "==================== Download RFC with P2P ====================";
        choicePrompt = "Please input your choice";
    }

    @Override
    public void display() {
        running = true;
        while (running) {
            int RFCNum = getNum("Please input RFC number");
            String hostname = getStringFromInput("Please input other peer's hostname");
            int uploadPort = getNum("Please input other peer's uploading port number");
            initPage();
            switch (getChoice()) {
                case 1:
                    if (P2PSystemClient.getPeerClient().getRFC(RFCNum, hostname, uploadPort)) {
                        running = false;
                    } else {
                        System.out.println("Failed to download RFC " + RFCNum + " from " + hostname + " at port " + uploadPort);
                    }
                    break;
                default:
                    running = false;
            }
        }
    }
}
