package core;

/**
 * Hold details about players, including their symbol on the board. Cannot be used directly and implementations must
 * override getMove()
 *
 * @author Bob Rzadzki
 * @version 2.0
 */

public abstract class Player {
    /** Symbols used for player tokens **/
    private static final char[] DEFAULT_SYMBOLS = {'X','O'};
    /** Pointer to the next symbol to use **/
    private static int NextChar = 0;

    /** This player's marker symbol **/
    private char mSymbol;
    /** This player's name (not yet implemented) **/
    private String mName;

    /** A pointer to the current game board object **/
    private Board board;

    /**
     * The default constructor, initializing the player's symbol to the next one available
     */
    public Player() {
        mSymbol = DEFAULT_SYMBOLS[(NextChar++)%DEFAULT_SYMBOLS.length];
    }

    /**
     * Get the player's marker symbol
     * @return the char representing the player on the board
     */
    public char getSymbol() { return mSymbol; }

    /**
     * Get the player's name
     * @return a String of the player's name (not yet implemented)
     */
    public String getName() { return mName; }

    /**
     * Set the player's name
     * @param n a string name for the player
     */
    public void setName(String n) { mName = n; }

    /**
     * Get a move from this player.
     * @return the column to play
     */
    public abstract int getMove();

    /**
     * Set the player's instance variable for the current game board.
     * @param b a game board object
     */
    public void setBoard(Board b) {
        board = b;
    }

    /**
     * Get a pointer to the game's board
     * @return the board on which this player is currently playing
     */
    protected Board getBoard() { return board; }
}
