package Pages.ClientPage;

import P2PSystem.P2PSystemClient;
import P2PSystem.RFC;
import Pages.BasePage;
import Pages.PageView;

public class AddRFCPage extends BasePage implements PageView {
    public AddRFCPage() {
        super();
        menu.add("Add");
        menu.add("Go back");
        pageTitle = "==================== Add RFC ====================";
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
                    RFC rfc = new RFC(RFCNum, title);
                    if (P2PSystemClient.getPeerClient().addRFC(rfc)) {
                        running = false;
                    } else {
                        System.out.println("Failed to add RFC " + RFCNum + " with title " + title);
                    }
                    break;
                default:
                    running = false;
            }
        }
    }
}
