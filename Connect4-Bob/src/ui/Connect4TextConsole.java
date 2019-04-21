package ui;

import core.Board;
import online.Connect4Client;
import online.Connect4Constants;
import online.Connect4OnlineInterface;

import java.io.IOException;
import java.util.Scanner;

/**
 * Provides a console interface to the Connect4 game class.
 *
 * @author Bob Rzadzki
 * @version 1.0
 */
public class Connect4TextConsole implements Connect4OnlineInterface, Connect4Constants {
    /** The game object that will handle all gameplay rules **/
    //static Connect4 game;
    Board board;
    Connect4Client client;
    int playerNumber;
    Scanner scanner;
    char token;
    int lastMoveColumn;

    Connect4Renderer renderer;


    public void main(String[] args) {
        scanner = new Scanner(System.in);

        System.out.println("===== Welcome to Connect4 =====");

        int playerCount = -1;
        do {
            System.out.println("Would you like to play a 1 or 2 player game? [1,2]");
            try {
                String input = scanner.nextLine();
                playerCount = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Don't be funny.");
            }
        } while(playerCount < 1 || playerCount > 2);

        board = new Board();

        renderer = new Connect4Renderer(board);

        client = new Connect4Client(this, playerCount == 2);
    }

    /**
     * Prompt the player that it's their turn and show input a move
     */
    @Override
    public void promptForMove() {
        int move = -1;
        while(move < 0 || move > 6) {
            System.out.println("Pick a column (1-7): ");
            move = scanner.nextInt() - 1;
        }

        try {
            client.sendMove(move);
            lastMoveColumn = move;
        } catch (IOException ex) {
            System.out.println("There was an error communicating with the server. Try again.");
        }


    }

    /**
     * After sending a move to the server, receive the result, either a row number or error code
     * @param row the row in which the chip landed
     */
    @Override
    public void receiveMoveResult(int row) {
        System.out.println("move result received: " + row);
        if(row != UNDEFINED && row != ERROR_ILLEGAL_MOVE) {
            board.setCell(token, lastMoveColumn, row);
            System.out.println(renderer.render());
        }
    }

    /**
     * Receive a move from this player or the other
     * @param player the player whose move this was
     * @param column the column in which the chip was played
     * @param row the row in which the chip landed
     */
    @Override
    public void receiveMove(int player, int column, int row) {
        System.out.println("Other player moves to column " + (column+1));
        board.setCell(player == PLAYER1 ? PLAYER1_TOKEN : PLAYER2_TOKEN, column, row);
        System.out.println(renderer.render());
    }

    /**
     * Indicate that the game has been won
     * @param playerNumber the Connect4Constant indicating which player won
     */
    @Override
    public void receiveWin(int playerNumber) {
        System.out.println("GAME OVER: YOU " + (playerNumber == this.playerNumber ? "WIN" : "LOSE") + "!");
    }

    /**
     * Indicate that the game has closed in a draw
     */
    @Override
    public void receiveDraw() {
        System.out.println("That's it; the game ends in a draw. Well played!");
    }

    /**
     * A player number has been received from the server, so
     *
     * @param playerNumber the Connect4Constants player number.
     */
    @Override
    public void receivePlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
        this.token = playerNumber == PLAYER1 ? PLAYER1_TOKEN : PLAYER2_TOKEN;
        if (this.playerNumber == PLAYER1) {
            System.out.println("You are player 1. Waiting for player 2.");
        } else {
            System.out.println("You are player 2. Waiting for player 1's move...");
        }
    }

    /**
     * Indicate to the user that their last attempted move was illegal
     */
    @Override
    public void warnIllegalMove() {
        System.out.println("That is an illegal move.");
    }

    /**
     * Indicate that the game has begun.
     */
    @Override
    public void gameStart() {
        System.out.println("Let the game begin.");
    }


}
