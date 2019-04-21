package ui;

import javafx.application.Application;

import java.util.Scanner;

/**
 * A simple prompt for the desired interface.
 */
public class Launcher {

    /**
     * The entry point which prompts a user for a choice of interfaces, then launches the selected one.
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        // Prompt for interface
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        do {
            System.out.println("Enter 1 to play in the console, 2 to play in the NEW! GUI environment: ");
            try {
                choice = scanner.nextInt();
            } catch (NumberFormatException ex) {
                System.out.println("Please enter either 1 or 2.");
            }
        }while(choice!=1 && choice!=2);

        if(choice == 1) {
            new Connect4TextConsole().main(null);
        } else {
            new Thread(() -> Application.launch(Connect4GUIActivity.class)).start();
        }
    }
}
