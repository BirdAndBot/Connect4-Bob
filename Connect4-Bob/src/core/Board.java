package core;

import java.util.ArrayList;

/**
 * Manages the board and legal moves in a game of Connect4. Responsible for determining "game over" states including wins.
 *
 * @author Bob Rzadzki
 * @version 1.2
 */

public class Board {
    /** The number of columns on the board **/
    int mColumns = 7,
    /** The number of rows on the board **/
        mRows    = 6;

    /** The game board as an array of chars, [column][row] **/
    private char[][] mGrid;
    /** Array of pointers to the next free slot in each column **/
    private int[] mColumnPointers;
    /** List for quick reference regarding which columns are available for play, updated as they fill up completely **/
    private ArrayList<Integer> mValidColumns;

    /** Char used to denote "no token in this slot" **/
    final public char NONE = ' ';
    /** Char used to denote "player 1's token in this slot" **/
    final public char PLAYER1 = 'X';
    /** Char used to denote "player 2's token in this slot" **/
    final public char PLAYER2 = 'O';

    /** A placeholder for either the winning player's marker or the NONE character up to the end of the game or in the event of a draw **/
    private char WINNER = NONE;

    private int[][] mWinSeries;

    /**
     * Default constructor which initializes instance variables to sane defaults
     */
    public Board() {
        mGrid = new char[mColumns][mRows];
        mValidColumns = new ArrayList<>();
        mColumnPointers = new int[mColumns];

        for(int c=0; c<mColumns; c++) {
            mValidColumns.add(c);
            mColumnPointers[c] = 0;
            for (int r = 0; r < mRows; r++) {
                mGrid[c][r] = NONE;
            }
        }
    }

    /**
     * Has the board been completely filled?
     * @return true if the board is full
     */
    private boolean isFull() { return playableColumns().size() == 0; }

    /**
     * The board is playable while it's not full and doesn't have a winner.
     * @return true if the board can still be played
     */
    public boolean isPlayable() { return !isFull() && WINNER==NONE; }
    /**
     * Play a chip into this column
     * @param player the character of the active player
     * @param column the column to play
     * @return true if the move is successful, false if it's for any reason illegal
     */
    public int insertAt(char player, int column) {
        if(columnIsPlayable(column)) {
            mGrid[column][mColumnPointers[column]] = player;

            checkForWinAt(column,mColumnPointers[column]);

            mColumnPointers[column]++;

            if(mColumnPointers[column] == mRows) mValidColumns.remove((Integer)column);

            return mColumnPointers[column]-1;
        }

        return -1;
    }

    /**
     * Look for a win starting at the given cell
     * @param column Column at which to start search
     * @param row Row at which to start search
     */
    private void checkForWinAt(int column, int row) {
        // A positive list of search directions, {y, x}
        // We'll invert this to search the opposite directions.
        int[][] directions = {
                {  1, 0 },
                {  1, 1 },
                {  0, 1 },
                { -1, 1 }
        };

        // Also store the series of chips that lead to the win, for another handy format of win display.
        ArrayList<Integer[]> winSeries = new ArrayList<>();

        char playerChar = mGrid[column][row];
        for(int[] dir : directions) {
            int r = row;
            int c = column;
            // First, back off in the opposite direction to the boundary of continuous markers
            do {
                r = r - dir[0];
                c = c - dir[1];
            } while(cellIsValid(c,r) && mGrid[c][r] == playerChar);

            int count = 0;
            while (cellIsValid(c + dir[1], r + dir[0])) {
                c += dir[1];
                r += dir[0];

                if (mGrid[c][r] == playerChar) {
                    Integer[] spot = { c, mRows - r - 1 };
                    winSeries.add(spot);
                    count++;
                }
                else break;
            }
            if (count >= 4) {
                // Set the win
                WINNER = playerChar;

                mWinSeries = new int[winSeries.size()][2];
                for(int s=0; s<winSeries.size(); s++) {
                    mWinSeries[s][0] = winSeries.get(s)[0];
                    mWinSeries[s][1] = winSeries.get(s)[1];
                }

                break;
            }
        }
    }

    /**
     * Check if these coordinates are within the realm of sanity.
     * @param column 0-based column
     * @param row 0-based row
     * @return true if this is a cell on the board, false if it's out of bounds
     */
    private boolean cellIsValid(int column, int row) {
        return (0 <= column && column < mColumns) && (0 <= row && row < mRows);
    }

    /**
     * True if this is a column on the board and it's not yet full.
     * @param column the column to check
     * @return true if this column is in bounds and not full
     */
    private boolean columnIsPlayable(int column) {
        return column >= 0 && column < mColumns && mColumnPointers[column] < mRows;
    }

    /**
     * Get the cell a piece will go to if this column is played
     * @param column the column to test
     * @return the bottom-most open cell in the column
     */
    public int playableCellInColumn(int column) {
        int i = -1;
        if(columnIsPlayable(column)) {
            i = mRows - mColumnPointers[column] - 1;
        }

        return i;
    }

    /**
     * Get a list of valid columns which are not yet full.
     * @return columns that are legal to play
     */
    private ArrayList<Integer> playableColumns() {
        return mValidColumns;
    }

    /**
     * Get a list of playable columns as an array.
     *
     * @return an array of 0-based column indexes suitable for play
     */
    public int[] getPlayableColumnsArray() {
        int[] p = new int[mValidColumns.size()];
        for(int i=0; i<mValidColumns.size(); i++) {
            p[i] = mValidColumns.get(i);
        }
        return p;
    }


    /**
     * If the game has been won, return the symbol of the winning player.
     * @return X or O for the winning player, EMPTY for a draw or ongoing game.
     */
    public char getWinner() {
        return WINNER;
    }

    /**
     * Immutable method of getting a copy of the game grid, useful for rendering the board state.
     * @return a duplicate of the working grid
     */
    public int[][] getGrid() {
        int[][] copy = new int[mColumns][mRows];
        for(int c=0; c<mColumns; c++) {
            for(int r=0; r<mRows; r++) {
                copy[c][r] = mGrid[c][r];
            }
        }
        return copy;
    }

    /**
     * Get the series of chips that led the winner to a win.
     * @return A series of between 4 and 7 {column,row} pairs
     */
    public int[][] getWinSeries() {
        return mWinSeries;
    }

    /**
     * Get the character at a given coordinate
     * @param column the column on the board
     * @param row the row within the column
     * @return a character, one of X, O or \0 representing the chip or absence of one on the board
     */
    public char getCharacterAt(int column, int row) {
        return mGrid[column][row];
    }

    /**
     * Get the number of columns on the board
     * @return the number of columns
     */
    public int getColumns() {
        return mColumns;
    }

    /** Get the number of rows on the board
     * @return the number of rows
     */
    public int getRows() {
        return mRows;
    }

    /**
     * Set a cell's contents directly, as by a call from the server in an online game
     * @param token a char indicating which token to place
     * @param column the column in which to place the token
     * @param row the row in which to place the token
     */
    public void setCell(char token, int column, int row) {
        if(cellIsValid(column, row)) mGrid[column][row] = token;

        // Update the column pointer
        for(int r=getRows()-1; r>0; r--) {
            if(mGrid[column][r] == NONE) {
                mColumnPointers[column] = r;
            }
        }

        checkForWinAt(column,row); // strictly for highlighting purpose
    }

}
