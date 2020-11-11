package Pages.ClientPage;

import P2PSystem.P2PSystemClient;
import Pages.BasePage;
import Pages.PageView;

public class LeavePage extends BasePage implements PageView {

    public LeavePage() {
        super();
        pageTitle = "==================== LEAVE SYSTEM ====================";
    }

    @Override
    public void display() {
        initPage();
        P2PSystemClient.getPeerClient().stop();
    }
}
