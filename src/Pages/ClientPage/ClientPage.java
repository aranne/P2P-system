package Pages.ClientPage;

import Pages.BasePage;
import Pages.PageView;

public class ClientPage extends BasePage implements PageView {


    public ClientPage() {
        super();
        menu.add("Join P2P System");
        menu.add("Add a new RFC");
        menu.add("Look up a certain RFC");
        menu.add("List all available RFC");
        menu.add("Download a new RFC");
        menu.add("Leave P2P System");
        pageTitle = "==================== Client =====================";
        choicePrompt = "Please input your choice:";
    }

    @Override
    public void display() {
        running = true;
        while (running) {
            initPage();
            switch (getChoice()) {
                case 1:
                    new JoinInPage().display();
                    break;
                case 2:
                    new AddRFCPage().display();
                    break;
                case 3:
                    new LookupRFCPage().display();
                    break;
                case 4:
                    new ListRFCPage().display();
                    break;
                case 5:
                    new GetRFCPage().display();
                    break;
                case 6:
            }
        }
    }
}
