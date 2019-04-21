package core;

import java.util.Arrays;
import java.util.Scanner;

/**
 * A player type which can be used at the console, prompting for and taking moves from standard input.
 *
 * @author Bob Rzadzki
 * @version 1.0
 */

public class ConsolePlayer extends Player {

    /** Scanner used for getting inputs **/
    private Scanner scanner;

    /**
     * Constructor for the console player. Internally sets the player's name to "Player [...]" where [...] is the next
     * available symbol.
     * @param s a scanner that will be used to gather the player's choices of moves
     */
    public ConsolePlayer(Scanner s) {
        super();
        scanner = s;
        setName("Player " + getSymbol());
    }

    /**
     * Uses a scanner (provided by the game class at construction) to ask the user's input
     * @return a 1-based column number representing the chosen column
     */
    @Override
    public int getMove() {
        System.out.println("Player " + getSymbol() + ", it is your turn. Choose a column " + getColumnString() + ": ");
        int choice=-1;
        try {
            String input = scanner.nextLine();
            choice = Integer.parseInt(input);
        } catch(NumberFormatException ex) {
            System.out.println("Please enter an integer.");
        }
        return choice-1;
    }

    /**
     * Get a formatted list of column choices to use in the player prompt
     * @return a bracket-enclosed, comma-delimited string of column numbers adjusted for 1-based index
     */
    private String getColumnString() {
        int[] columns = getBoard().getPlayableColumnsArray();
        for(int i=0; i<columns.length; i++)
            columns[i] = columnToChoice(columns[i]);
        return Arrays.toString(columns);
    }

    /**
     * Columns in the game are 0-based, but we'll take user input as 1-based; this is a helper method to affect that.
     * @param c The board column
     * @return A column number adjusted up by 1 to match the user choices.
     */
    private static int columnToChoice(int c) { return c + 1;}
}
