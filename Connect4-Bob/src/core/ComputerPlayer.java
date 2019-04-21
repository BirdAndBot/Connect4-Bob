package core;
import java.util.Random;

/**
 * A computer player which will make random moves on its turn.
 *
 * @author Bob Rzadzki
 * @version 1.1
 */

public class ComputerPlayer extends Player {

    /** A random number generator used to pick moves **/
    Random random;

    /**
     * Standard constructor. Internally sets the player's name to "Computer player" and initializes a random
     * number generator.
     */
    public ComputerPlayer() {
        super();
        random = new Random();
        setName("Computer player");
    }

    /**
     * From the game Board's list of available columns, chooses one at random.
     * @return an integer representing the column numbers
     */
    @Override
    public int getMove() {
        int[] moves = getBoard().getPlayableColumnsArray();
        int choice = moves[random.nextInt(moves.length)];
        return choice;
    }
}
