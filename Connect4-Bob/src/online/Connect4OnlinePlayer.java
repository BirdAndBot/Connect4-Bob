package online;

import core.Player;

import java.net.Socket;

/**
 * A simple extension of the Player class for use in online games
 *
 * @author Bob Rzadzki
 * @version 1.0
 */
public class Connect4OnlinePlayer extends Player {

    private Socket socket;
    int playerNumber;

    public Connect4OnlinePlayer(int playerNumber, Socket s) {
        socket = s;
        this.playerNumber = playerNumber;
    }

    @Override
    public int getMove() {
        return 0;
    }

    public Socket getSocket() { return socket; }

    public int getPlayerNumber() { return playerNumber; }

}
