package Pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BasePage {
    protected Scanner scanner = new Scanner(System.in);
    protected String pageTitle = "";
    protected String choicePrompt = "";
    protected List<String> menu = new ArrayList<>();
    protected boolean running = false;

    protected void initPage() {
        show(pageTitle);
        show(choicePrompt);
        show(menu);
    }

    protected void show(String s) {
        if (s != null && s.length() > 0) {
            System.out.println(s);
        }
    }
    protected void show(List<String> menu) {
        for (int i = 0; i < menu.size(); i++) {
            System.out.printf("%d.\t%s\n", i + 1, menu.get(i));
        }
        System.out.println();
    }

    protected int getChoice() {
        int index;
        while (true) {
            try {
                String choice = scanner.nextLine();
                index = Integer.parseInt(choice);
            } catch (Exception e) {
                System.out.println("Invalid Menu id");
                continue;
            }
            if (index < 1 || index > menu.size()) {
                System.out.println("Invalid Menu id");
            } else {
                break;
            }
        }
        return index;
    }

    protected String getStringFromInput(String prompt) {
        show(prompt);
        return scanner.nextLine();
    }

    protected int getNum(String prompt) {
        int num = 0;
        while (true) {
            try {
                System.out.println(prompt);
                num = Integer.parseInt(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Invalid number");
            }
        }
        return num;
    }
}
