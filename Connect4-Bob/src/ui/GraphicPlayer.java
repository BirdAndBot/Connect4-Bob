package ui;

import core.Player;

/**
 * A minimal implementation of the abstract Player class for use in the GUI.
 */
public class GraphicPlayer extends Player {

    /**
     * A stub method - do not use. If this is relied on to gather moves the way it is for ComputerPlayer or
     * ConsolePlayer, all chips for the player will go in the first column. There will also be no delay in asking
     * for moves, so human players won't have a chance to input anything and a 1- or 2-player game will end after 6
     * iterations of the game loop.
     * @return 0 every time
     */
    @Override
    public int getMove() {
        return 0;
    }
}
