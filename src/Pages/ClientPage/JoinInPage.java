package Pages.ClientPage;

import P2PSystem.P2PSystemClient;
import P2PSystem.PeerClient;
import Pages.BasePage;
import Pages.PageView;

public class JoinInPage extends BasePage implements PageView {

    public JoinInPage() {
        super();
        menu.add("Connect");
        menu.add("Go back");
        pageTitle = "==================== CONNECT TO SERVER ====================";
        choicePrompt = "Please input your choice";
    }


    @Override
    public void display() {
        running = true;
        while (running) {
            String hostname = getStringFromInput("Please input server hostname");
            int uploadPort = getNum("Please input client upload port number");
            initPage();
            switch (getChoice()) {
                case 1:
                    P2PSystemClient.setPeerClient(new PeerClient(hostname, uploadPort));
                    try {
                        P2PSystemClient.getPeerClient().start();
                        new ClientPage().display();
                        running = false;
                    } catch (RuntimeException e) {
                        show(e.getMessage());
                    }
                    break;
                default:
                    running = false;
            }
        }
    }
}
