package ui;

import core.Board;

/**
 * Renders a Connect4 board as console output.
 *
 * @author Bob Rzadzki
 * @version 1.0
 */
public class Connect4Renderer {
    /** The game whose board to render **/
    //final Connect4 game;
    final Board board;

    /** Simple constructor that sets the game instance variable
     *
     * @param b the board to render
     */
    public Connect4Renderer(Board b) {
        board = b;
    }

    /**
     * Generate a text-based graphic representation of the game board:
     *
     * | | | | | | | |
     * | | | |O|X| | |
     * |O| | |X|O| | |
     * |X|X|X|X|O| | |
     * |X|O|O|X|O|O| |
     * |X|X|X|O|X|O|O|
     *  1 2 3 4 5 6 7
     *
     *  This will include all markers that have been placed on the board as well as column labels for all
     *  columns still available for play.
     *
     * @return a string representation of the state of the game board
     */
    public String render() {
        String ret = "";
        int[][] grid = board.getGrid();

        for(int r=grid[0].length-1; r>=0; r--) {
            for(int c=0; c<grid.length; c++) {
                ret += "|" + (char)grid[c][r];
            }
            ret += "|\n";
        }
        // Add column labels
        int[] validColumns = board.getPlayableColumnsArray();
        for(int c=0; c<grid.length; c++) {
            ret += " " + (intArrayContains(validColumns,c) ? (c+1) : " ");
        }

        return ret;
    }

    /**
     * Search an array of ints for the presence of an int. Useful in determining which columns are in the
     * list of columns available for play.
     * @param array the array to search
     * @param search the int to search for
     * @return true if the int is in the array, false if it's not
     */
    private boolean intArrayContains(int[] array, int search) {
        for(int i : array) {
            if (i == search) return true;
        }
        return false;
    }
}
