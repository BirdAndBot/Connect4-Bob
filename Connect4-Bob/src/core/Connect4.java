package core;

/**
 * Manages the state of play in a game of Connect4
 *
 * @author Bob Rzadzki
 * @version 1.0
 */

public class Connect4 {
    /** Used for tracking the overall state of the game **/
    public enum GAME_STATE {
        /** The game will be in the PLAYING state until a win or draw condition **/
        PLAYING,
        /** The game is over when it's won or a draw is declared **/
        OVER
    }

    /** Used to define a winner or draw condition at the end of the game **/
    public enum WINNER {
        PLAYER1, PLAYER2, DRAW
    }

    /** Holds the current state of the game **/
    private GAME_STATE GameState;
    /** At the end of the game, stores the winning player **/
    private WINNER Winner;
    /** The game board used during play **/
    private Board board;

    /** Tracks the details of player 1 **/
    private Player player1,
    /** Tracks the details of player 2 **/
    player2;
    /** Index of the current player, alternates between 0 (player 1) and 1 (player 2) **/
    private int currentPlayer = 0;

    /**
     * Default constructor, initializing instance variables to sane defaults
     *
     * @param p1 an object representing the first player
     * @param p2 an object representing the second player
     */
    public Connect4(Player p1, Player p2) {
        board = new Board();

        player1 = p1;
        p1.setBoard(board);
        player2 = p2;
        p2.setBoard(board);

        GameState = GAME_STATE.PLAYING;
    }

    /**
     * Get the object corresponding to the player on whom the game is currently waiting to make a move
     * @return a player object for the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer == 0 ? player1 : player2;
    }

    /**
     * Make a move with the current player
     * @param column the column in which to place a marker
     * @return true if the move is legal (the column is in bounds and not full), false otherwise
     */
    public int makeMove(int column) {
        if(GameState == GameState.OVER) return -1;
        int result = board.insertAt(getCurrentPlayer().getSymbol(),column);
        if(result != -1) {
            updateGameState();
        }
        return result ;
    }

    /**
     * From the state of the board, determines if the game has ended and if so, who has won. If the game is still in
     * progress, updates the index of the current player.
     */
    private void updateGameState() {
        if(!board.isPlayable()) {
            GameState = GameState.OVER;
            if(board.getWinner() == board.NONE) Winner = WINNER.DRAW;
            else if(board.getWinner() == player1.getSymbol()) Winner = WINNER.PLAYER1;
            else if(board.getWinner() == player2.getSymbol()) Winner = WINNER.PLAYER2;
        } else {
            currentPlayer = (currentPlayer + 1) % 2;
        }
    }

    /**
     * Is the game still in progress?
     * @return true if the game hasn't ended
     */
    public boolean isPlayable() {
        return GameState == GAME_STATE.PLAYING;
    }

    /**
     * Get the player object corresponding to the winner.
     * @return the Player object representing the winner if the game has ended in a win, or null if it's ongoing or
     * ended in a draw
     */
    public Player getWinner() {
        switch(Winner) {
            case PLAYER1:
                return player1;
            case PLAYER2:
                return player2;
            default:
                return null;
        }
    }

    /**
     * Has this game ended in a draw?
     * @return true if the game ended in a draw; false if it's ongoing or was won
     */
    public boolean isDraw() {
        return Winner == WINNER.DRAW;
    }

    /**
     * Get the game board
     * @return the game's Board object
     */
    public Board getBoard() { return board; }

    public int getColumns() { return board.getColumns(); }
    public int getRows() { return board.getRows(); }

}
