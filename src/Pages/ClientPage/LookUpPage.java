package Pages.ClientPage;

import P2PSystem.P2PSystemClient;
import Pages.BasePage;
import Pages.PageView;

public class LookUpPage extends BasePage implements PageView {
    public LookUpPage() {
        super();
        menu.add("Look up");
        menu.add("Go back");
        pageTitle = "==================== Look up RFC on server ====================";
        choicePrompt = "Please input your choice";
    }

    @Override
    public void display() {
        running = true;
        while (running) {
            int RFCNum = getNum("Please input RFC number");
            String title = getStringFromInput("Please input RFC title");
            initPage();
            switch (getChoice()) {
                case 1:
                    if (P2PSystemClient.getPeerClient().lookupRFC(RFCNum, title)) {
                        running = false;
                    } else {
                        System.out.println("Failed to look up RFC " + RFCNum + " with title " + title);
                    }
                    break;
                default:
                    running = false;
            }
        }
    }
}
