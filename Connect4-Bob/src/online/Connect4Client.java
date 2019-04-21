package online;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * A client object handling communication between the server and an implementation of the Connect4OnlineInterface.
 *
 * @author Bob Rzadzki
 * @version 1.0
 */
public class Connect4Client implements Connect4Constants {
    // Input and output streams from/to server
    private DataInputStream fromServer;
    private DataOutputStream toServer;

    // Continue to play?
    private boolean continueToPlay = true;

    // Wait for the player to mark a cell
    private boolean waiting = true;
    private boolean waitingForResult = false;

    // Host name or ip
    private String host = "localhost";

    private int playerNumber;

    private Connect4OnlineInterface ui;

    public Connect4Client(Connect4OnlineInterface ui, boolean playAgainstPerson) {
        this.ui = ui;
        // Connect to the server
        connectToServer(playAgainstPerson);
    }

    private void connectToServer(boolean playAgainstPerson) {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(host, 8004);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        // Control the game on a separate thread
        new Thread(() -> {
            try {
                // Tell server what kind of game to play
                toServer.writeInt(playAgainstPerson ? PLAY_AGAINST_PERSON : PLAY_AGAINST_COMPUTER);

                // Get player assignment from the server
                int player = fromServer.readInt();
                playerNumber = player;

                ui.receivePlayerNumber(playerNumber);

                // Continue to play
                while (continueToPlay) {
                    receiveInfoFromServer();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Send this player's move to the server
     * @param columnSelected the column this player wants to play
     * @throws IOException if there's a problem communicating with the server
     */
    public void sendMove(int columnSelected) throws IOException {
        toServer.writeInt(columnSelected); // Send the selected column
        waitingForResult = true;
    }

    /**
     * Receive info from the server
     * @throws IOException if there's a problem communicating with the server
     */
    private void receiveInfoFromServer() throws IOException {
        // Receive game status
        int status = fromServer.readInt();

        int column, row, player;

        /** Switch based on the message contents, calling methods on the Interface appropriately **/
        switch(status) {
            case START:
                ui.gameStart();
                break;
            case WIN:
                int winner = fromServer.readInt();
                ui.receiveWin(winner);
                break;
            case DRAW:
                ui.receiveDraw();
                player = fromServer.readInt();
                column = fromServer.readInt();
                row    = fromServer.readInt();
                ui.receiveMove(player, column, row);
                break;
            case MOVE:
                player = fromServer.readInt();
                column = fromServer.readInt();
                row    = fromServer.readInt();
                ui.receiveMove(player, column, row);
                break;
            case PROMPT_FOR_MOVE:
                ui.promptForMove();
                break;
            default:
                if(waitingForResult) {
                    ui.receiveMoveResult(status);
                    waitingForResult = false;
                }
        }
    }
}
