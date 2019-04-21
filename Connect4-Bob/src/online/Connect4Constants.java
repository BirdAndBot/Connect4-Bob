package online;

/**
 * A collection of integers for passing messages between client and server
 */
public interface Connect4Constants {

    /* 0-6 are reserved for indicating columns and rows */

    public static final int UNDEFINED = -1;

    public static final int ERROR_ILLEGAL_MOVE = 100;

    public static final int PLAYER1 = 10;
    public static final int PLAYER2 = 11;

    public static final int WAITING_FOR_YOU = 13;
    public static final int WAITING_FOR_OTHER_PLAYER = 14;

    public static final int MOVE = 15;
    public static final int PROMPT_FOR_MOVE = 16;

    public static final int WAITING = 20;
    public static final int DRAW    = 21;
    public static final int WIN     = 22;
    public static final int START = 23;

    public static final int PLAYER1_WON = 30;
    public static final int PLAYER2_WON = 31;

    public static final char PLAYER1_TOKEN = 'X';
    public static final char PLAYER2_TOKEN = 'O';

    public static final int PLAY_AGAINST_PERSON = 60;
    public static final int PLAY_AGAINST_COMPUTER = 61;
}
