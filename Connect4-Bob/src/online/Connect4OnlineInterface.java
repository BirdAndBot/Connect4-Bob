package online;

/**
 * A set of methods for user interfaces to receive events from the Connect4Client
 *
 * @version 1.0
 * @author Bob Rzadzki
 */
public interface Connect4OnlineInterface {
    /**
     * The other player has made a move, add it to the local game object, set the local player's turn and refresh the view.
     * @param player the constant indicating the player who made the move
     * @param column the column played
     * @param row the row where the move landed
     */
    void receiveMove(int player, int column, int row);

    /**
     * It is the local player's move; put the UI in a waiting state until they make one.
     */
    void promptForMove();

    /**
     * Someone has won the game; set the winner and show the win state
     * @param playerNumber the Connect4Constant indicating which player won
     */
    void receiveWin(int playerNumber);

    /**
     * The game has ended in a draw; update the UI to that effect
     */
    void receiveDraw();

    /**
     * We've been assigned a player number by the server; set up the UI to that effect.
     * @param playerNumber the Connect4Constants player number.
     */
    void receivePlayerNumber(int playerNumber);

    /**
     * Warn the user that their last attempted move was not allowed
     */
    void warnIllegalMove();

    /**
     * Indicate that the game has begun
     */
    void gameStart();

    /**
     * After sending a move to the server, receive the results of that move, either a row number or error code
     * @param row the row in which the chip lands
     */
    void receiveMoveResult(int row);
}
