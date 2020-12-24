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
                    P2PSystemClient.getPeerClient().start();
                    running = false;
                    break;
                default:
                    running = false;
            }
        }
    }
}
