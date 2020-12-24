package Pages.ClientPage;

import P2PSystem.P2PSystemClient;
import Pages.BasePage;
import Pages.PageView;

public class ListRFCPage extends BasePage implements PageView {
    public ListRFCPage() {
        super();
        pageTitle = "==================== ALL RFCs ====================";
        choicePrompt = "Please wait for response";
    }

    @Override
    public void display() {
        running = true;
        while (running) {
            initPage();
            if (P2PSystemClient.getPeerClient().listRFCs()) {
                running = false;
            } else {
                System.out.println("Failed to list all RFC on central server");
            }
        }
    }
}
