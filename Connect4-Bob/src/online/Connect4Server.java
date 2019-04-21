package online;

import core.ComputerPlayer;
import core.Connect4;
import core.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


/**
 * A server which will thread off sessions for 1- or 2-player games as players connect.
 *
 * @author Bob Rzadzki
 * @version 1.0
 */
public class Connect4Server implements Connect4Constants {

    /** A sequential number for the session **/
    private int sessionNo = 0; // Number a session

    /**
     * The default constructor that will handle session threading.
     */
    public Connect4Server() {
        new Thread( () -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8004);
                System.out.println(new Date() + ": Server started at socket 8004\n");

                // Ready to create a session for every two players
                while (true) {
                    sessionNo++;
                    System.out.println(new Date() + ": Wait for players to join session " + sessionNo + '\n');

                    // Connect to player 1
                    Socket player1 = serverSocket.accept();

                    System.out.println(new Date() + ": Player 1 joined session " + sessionNo + '\n');
                    System.out.println("Player 1's IP address: " + player1.getInetAddress().getHostAddress() + '\n');


                    // Notify that the player is Player 1
                    new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

                    int playerChoice = new DataInputStream(player1.getInputStream()).readInt();
                    Socket player2;
                    if(playerChoice == PLAY_AGAINST_COMPUTER) {
                        System.out.println("Player 1 in session " + sessionNo + " opts to play against computer");
                        player2 = null;
                        // Launch a new thread for this session of two players
                        new Thread(new HandleASession(player1, player2)).start();
                    } else {
                        System.out.println("Player 1 in session " + sessionNo + " opts to play against human; waiting for connection...");
                        // Connect to player 2
                        player2 = serverSocket.accept();


                        System.out.println(new Date() + ": Player 2 joined session " + sessionNo + '\n');
                        System.out.println("Player 2's IP address: " + player2.getInetAddress().getHostAddress() + '\n');


                        // Notify that the player is Player 2
                        new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);


                        // Display this session and increment session number
                        System.out.println(new Date() + ": Start a thread for session " + sessionNo++ + '\n');

                        // Launch a new thread for this session of two players
                        new Thread(new HandleASession(player1, player2)).start();
                    }
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * A Runnable implementation for handling a game session
     */
    class HandleASession implements Runnable, Connect4Constants {
        /** Instance variables to hold some player details **/
        private Connect4OnlinePlayer player1, player2;

        /** A computer player to be used in 1-player games **/
        private ComputerPlayer computerPlayer;

        /** The Connect4 which will handle all gameplay logic **/
        Connect4 game;

        /** For handling data coming from player 1 **/
        private DataInputStream fromPlayer1;
        /** For data going out to player 1 **/
        private DataOutputStream toPlayer1;
        /** For handling data coming from player 2 **/
        private DataInputStream fromPlayer2;
        /** For data going out to player 2 **/
        private DataOutputStream toPlayer2;

        /** A handy flag for whether the second player is human after all **/
        boolean player2IsComputer = false;

        // Continue to play
        //private boolean continueToPlay = true;

        /**
         * Construct a thread
         * @param player1Socket the socket object for communicating with player1
         * @param player2Socket the socket object for communicating with player2 or null for a 1-player game
         */
        public HandleASession(Socket player1Socket, Socket player2Socket) {

            this.player1 = new Connect4OnlinePlayer(PLAYER1, player1Socket);

            if(player2Socket == null) {
                // create a computer player
                this.computerPlayer = new ComputerPlayer();
                player2IsComputer = true;
                game = new Connect4(player1,computerPlayer);
            } else {
                this.player2 = new Connect4OnlinePlayer(PLAYER2, player2Socket);
                game = new Connect4(player1,player2);
            }

        }

        /** Implement the run() method for the thread */
        public void run() {
            try {
                // Create data input and output streams
                fromPlayer1 = new DataInputStream(player1.getSocket().getInputStream());
                toPlayer1 = new DataOutputStream(player1.getSocket().getOutputStream());
                toPlayer1.writeInt(START);

                if(!player2IsComputer) {
                    fromPlayer2 = new DataInputStream(player2.getSocket().getInputStream());
                    toPlayer2 = new DataOutputStream(player2.getSocket().getOutputStream());
                    toPlayer2.writeInt(START);
                    while(fromPlayer2.available()>0) fromPlayer2.readInt();
                }



                // Continuously serve the players and determine and report
                // the game status to the players
                while (true) {
                    // Receive a move from player 1
                    toPlayer1.writeInt(PROMPT_FOR_MOVE);
                    int column, row;
                    do {
                        column = fromPlayer1.readInt();
                        System.out.println("Player1's buffer to the server contains: ");
                        //while(fromPlayer1.available()>0) {
                        //    System.out.println("\t" + fromPlayer1.readInt());
                        //}
                        row = game.makeMove(column);
                        if(row == -1) toPlayer1.writeInt(ERROR_ILLEGAL_MOVE);
                    } while(row == -1);
                    toPlayer1.writeInt(row);

                    System.out.println("Player1 moves to c" + column + "r" + row);

                    if(!player2IsComputer) {
                        // Send player 1's selected row and column to player 2
                        sendMove(toPlayer2, PLAYER1, column, row);
                    }

                    // Check if Player 1 wins
                    if (!game.isPlayable()) {
                        if(game.isDraw()) {
                            // send the draw state to both players
                            sendDraw();
                        } else {
                            Player winner = game.getWinner();
                            int winnerConstant = winner == player1 ? PLAYER1 : PLAYER2;
                            sendWin(winnerConstant);
                        }
                        break; // Break the loop
                    }


                    if(player2IsComputer) {
                        // Get a computer move, propagate it to player 1
                        column = computerPlayer.getMove();
                        row = game.makeMove(column);
                        System.out.println("Player2 computer moves to c" + column + "r" + row);
                        sendMove(toPlayer1, PLAYER2, column, row);
                    } else {
                        toPlayer2.writeInt(PROMPT_FOR_MOVE);

                        do {
                            column = fromPlayer2.readInt();
                            row = game.makeMove(column);
                            System.out.println("Player2 goes to column " + column + " row " + row);
                            if (row == -1) toPlayer2.writeInt(ERROR_ILLEGAL_MOVE);
                        } while (row == -1);
                        toPlayer2.writeInt(row);

                        // Send player 2's move to player 1 to update their board
                        sendMove(toPlayer1, PLAYER2, column, row);
                        System.out.println("Player2 moves to c" + column + "r" + row);
                    }

                    // Check if Player 2 wins
                    if (!game.isPlayable()) {
                        if (game.isDraw()) {
                            // send the draw state to both players
                            sendDraw();
                        } else {
                            Player winner = game.getWinner();
                            int winnerConstant = winner == player1 ? PLAYER1 : PLAYER2;
                            sendWin(winnerConstant);
                        }
                        break; // Break the loop
                    }
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }

        /**
         * Send the move to other player
         * @param out the stream to which this move will be sent
         * @param player the constant indicating the player who made the move
         * @param column the column that was played
         * @param row the row where the move landed
         * @throws IOException if there's a problem communicating with the server
         */
        private void sendMove(DataOutputStream out, int player, int column, int row) throws IOException {
            out.writeInt(MOVE);
            out.writeInt(player);
            out.writeInt(column); // Send column index
            out.writeInt(row); // Send row index

        }

        /**
         * Send an indicator the game has closed in a draw
         * @throws IOException if there's a problem communicating with the server
        */
        private void sendDraw() throws IOException {
            toPlayer1.writeInt(DRAW);
            if(!player2IsComputer) {
                toPlayer2.writeInt(DRAW);
            }
        }

        /**
         * Send an indicator the game has been won and by whom *
         * @param winnerConstant the constant indicating the winning player
         * @throws IOException if there's a problem communicating with the server
         */
        private void sendWin(int winnerConstant) throws IOException {
            toPlayer1.writeInt(WIN);
            toPlayer1.writeInt(winnerConstant);

            if(!player2IsComputer) {
                toPlayer2.writeInt(WIN);
                toPlayer2.writeInt(winnerConstant);
            }
        }

    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        new Connect4Server();
    }
}
