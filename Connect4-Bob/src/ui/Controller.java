package ui;

import core.Board;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import online.Connect4Client;
import online.Connect4Constants;
import online.Connect4OnlineInterface;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * A controller for the main game view, where all logic for the interface resides
 */
public class Controller implements Initializable, Connect4OnlineInterface, Connect4Constants {

    private Board board;
    private Connect4Client client;
    private boolean playing = false;
    private boolean twoPlayers;
    private int playerNumber;
    private boolean myTurn = false;
    private char myToken;
    private int lastMoveColumn;

    /** The pane housing all the player select controls **/
    @FXML
    public Pane panePlayerSelect;

    /** The pane containing our custom gameboard control **/
    @FXML
    public Pane gameBoardContainer;

    /** A global GameBoard object we'll replace for every new game **/
    GameBoard gameBoard;

    /** Status label to display a welcome message, game state hints, etc. **/
    @FXML
    public Label statusLabel;

    @FXML
    public Label playerLabel;

    /** A player can click this to start the game over at the player select screen **/
    @FXML
    public Button playAgainButton;

    /** Default, no-argument constructor **/
    public Controller() {}

    /**
     * Setup the absolute essentials of the window and set the state of dynamic elements
     * @param url pointing to the layout file
     * @param resourceBundle where resources like layout files and images are kept
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusLabel.setText(null);
        setupStatePlayerSelect();
    }

    /**
     * Make visible controls for selecting the number of players, hide all unrelated controls.
     */
    @FXML
    private void setupStatePlayerSelect() {
        panePlayerSelect.setVisible(true);
        gameBoardContainer.setVisible(false);
        statusLabel.setText("Get ready to rumble.");
        playAgainButton.setVisible(false);
    }

    /**
     * Hide player select, setup the game, make the game board visible.
     */
    private void setupStatePlay() {
        client = new Connect4Client(this,twoPlayers);
        board = new Board();
        gameBoard = new GameBoard();
        gameBoardContainer.getChildren().clear();
        gameBoardContainer.getChildren().add(gameBoard);

        gameBoardContainer.setVisible(true);
        playAgainButton.setVisible(false);
        panePlayerSelect.setVisible(false);

        statusLabel.setText("Waiting for server...");
    }

    /**
     * Call the more general method for setting up players and getting the game into a ready state.
     */
    @FXML
    private void setupTwoPlayers() {
        setupPlayers(true);
    }

    /**
     * Call the more general method for setting up players and getting the game into a ready state.
     */
    @FXML
    private void setupOnePlayer() {
        setupPlayers(false);
    }

    /**
     * Setup the game for one or two players and then move into the PLAY state.
     * @param twoPlayers true if a two-player game is desired; false for player-vs-computer
     */
    private void setupPlayers(boolean twoPlayers) {
        this.twoPlayers = twoPlayers;

        setupStatePlay();
    }

    /**
     * Receive a move made by the other player
     * @param player the constant indicating which player this is for
     * @param column the column in which the new chip lands
     * @param row the row in which the new chip lands
     */
    @Override
    public void receiveMove(int player, int column, int row) {
        System.out.println("Received player move: c"+column+"r"+row);
        board.setCell((player == PLAYER1 ? PLAYER1_TOKEN : PLAYER2_TOKEN), column, row);
        Platform.runLater(() -> gameBoard.columns[column].update());
    }

    /**
     * Indicate that the server is prompting the user for a move.
     */
    @Override
    public void promptForMove() {
        myTurn = true;
        Platform.runLater(() -> statusLabel.setText("It's your turn."));
    }

    /**
     * Indicate that the game has been won
     * @param playerNumber the Connect4Constant indicating which player won
     */
    @Override
    public void receiveWin(int playerNumber) {
        playing = false;
        Platform.runLater(() -> {
            // Highlight the winning series
            int[][] winSeries = board.getWinSeries();
            for (int s = 0; s < winSeries.length; s++) {
                int[] spot = winSeries[s];
                gameBoard.columns[spot[0]].cells[spot[1]].highlight();
            }

            statusLabel.setText("Player " + (playerNumber == PLAYER1 ? PLAYER1_TOKEN : PLAYER2_TOKEN) + " wins!");
            playAgainButton.setVisible(true);
        });
    }

    /**
     * Indicate the game has closed in a draw
     */
    @Override
    public void receiveDraw() {
        playing = false;
        Platform.runLater(() -> {
            statusLabel.setText("Alright, we'll call it a draw!");
            playAgainButton.setVisible(true);
        });
    }

    /**
     * Receive and set the indicator for which player we are, 1 or 2
     * @param playerNumber the Connect4Constants player number.
     */
    @Override
    public void receivePlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
        myToken = playerNumber == PLAYER1 ? PLAYER1_TOKEN : PLAYER2_TOKEN;
        Platform.runLater(()->playerLabel.setText("Player " + myToken));
    }

    /**
     * Display a warning to the player that their choice of move wasn't legal
     */
    @Override
    public void warnIllegalMove() {

    }

    /**
     * Indicate that the game has begun
     */
    @Override
    public void gameStart() {
        playing = true;

    }

    /**
     * After sending a move to the server, receive the result of that move, either a row number or an error.
     * @param row the row in which the chip landed
     */
    @Override
    public void receiveMoveResult(int row) {
        if(row != ERROR_ILLEGAL_MOVE && row != UNDEFINED) {
            board.setCell(myToken, lastMoveColumn, row);
            gameBoard.columns[lastMoveColumn].update();
            System.out.println("Board updated");
        } else {
            myTurn = true;
        }
    }

    /**
     * A custom control that houses the game grid and related behaviors.
     */
    class GameBoard extends HBox {
        /** An array of Column objects we'll update as the game progresses **/
        Column[] columns;

        /**
         * Default constructor which MUST be called after the Connect4 game object is defined.
         */
        public GameBoard() {
            super();

            AnchorPane.setBottomAnchor(this,0.0);
            AnchorPane.setRightAnchor(this,0.0);
            AnchorPane.setTopAnchor(this,0.0);
            AnchorPane.setLeftAnchor(this,0.0);

            columns = new Column[board.getColumns()];
            // For each column, add a column to our collection
            for(int c=0; c<columns.length; c++) {
                Column column = new Column(c);
                columns[c] = column;
            }
            this.getChildren().addAll(columns);
        }

        /**
         * A custom control that handles mouse-based game moves and updating the grid visually.
         */
        class Column extends VBox {
            /** An array of cells that will display game chips **/
            Cell[] cells;
            /** The number of this column on the game board, used for coordinates **/
            int columnNumber;

            /**
             * The constructor, which sets up mouse events and populates our Cell array
             * @param c the index of this column on the game board (0-based)
             */
            public Column(int c) {
                super();

                columnNumber = c;
                cells = new Cell[board.getRows()];
                for (int r = 0; r < board.getRows(); r++) {
                    Cell cell = new Cell();
                    cells[r] = cell;
                }
                this.getChildren().addAll(cells);

                this.setOnMouseClicked(mouseEvent -> handleMouseClick());

                this.setOnMouseEntered(mouseEvent -> handleMouseEntered());

                this.setOnMouseExited(mouseEvent -> handleMouseExited());
            }

            /**
             * Call this to update the cells in this column to match the Connect4 game object.
             */
            public void update() {
                // Work backwards up the row
                for(int r=0; r<board.getRows(); r++) {
                    cells[cells.length-r-1].setToken(board.getCharacterAt(columnNumber,r));
                }
            }

            /**
             * Handle a click event by attempting to put a marker in the current column
             */
            private void handleMouseClick() {
                // The move will succeed if this column is in play
                if(myTurn) {
                    try {
                        client.sendMove(columnNumber);
                        lastMoveColumn = columnNumber;
                        myTurn = false;
                    } catch (IOException ex) {
                        System.out.println("There was a problem communicating with the server. Try again.");
                    }
                }
            }

            /**
             * When the mouse passes over this column, indicate where a chip would go if played. Only do this if the
             * game is still playable, ie., not won or totally filled.
             */
            private void handleMouseEntered() {
                if(playing && myTurn) {
                    int freeCell = board.playableCellInColumn(columnNumber);
                    if (freeCell > -1) {
                        cells[freeCell].showToken(myToken);
                    }
                }
            }

            /**
             * If the game is still playable, hide any chips we might be previewing in the column.
             */
            private void handleMouseExited() {
                if(playing && myTurn) {
                    int freeCell = board.playableCellInColumn(columnNumber);
                    if (freeCell > -1) {
                        cells[freeCell].hideToken();
                    }
                }
            }

            /**
             * Sourced in part from the TicTacToe example, the Cell displays either X or O as shapes and provides
             * methods for switching between them. Also handles grayed-out "preview" mode and highlighted win series.
             */
            public class Cell extends StackPane {

                /** These two panes will contain the generated shapes, making for quick switches between them. **/
                private Pane xPane, oPane;

                /**
                 * Default constructor which handle some setup and styling.
                 */
                public Cell()
                {
                    setStyle("-fx-border-color: black");
                    this.setPrefSize(2000, 2000);
                    setupPanes();
                }

                /**
                 * Generate the two overlapping panes that will display the player chips when needed. They are created
                 * in the grayed-out state, which will be used most often during previews, and then made invisible
                 * to start.
                 */
                private void setupPanes() {
                    xPane = new Pane();
                    xPane.setOpacity(0.5);
                    xPane.setVisible(false);
                    xPane.setStyle("-fx-stroke: black");
                    Line line1 = new Line(10, 10, this.getWidth() - 10, this.getHeight() - 10);
                    line1.endXProperty().bind(this.widthProperty().subtract(10));
                    line1.endYProperty().bind(this.heightProperty().subtract(10));
                    line1.setStyle("-fx-stroke: inherit");
                    line1.setStrokeWidth(5);

                    Line line2 = new Line(10, this.getHeight() - 10, this.getWidth() - 10, 10);
                    line2.startYProperty().bind(this.heightProperty().subtract(10));
                    line2.endXProperty().bind(this.widthProperty().subtract(10));
                    line2.setStyle("-fx-stroke: inherit");
                    line2.setStrokeWidth(5);

                    // Add the lines to the pane
                    xPane.getChildren().addAll(line1, line2);
                    this.getChildren().add(xPane);

                    oPane = new Pane();
                    oPane.setOpacity(0.5);
                    oPane.setVisible(false);
                    oPane.setStyle("-fx-stroke: black");
                    Ellipse ellipse = new Ellipse(this.getWidth() / 2,
                            this.getHeight() / 2, this.getWidth() / 2 - 10,
                            this.getHeight() / 2 - 10);
                    ellipse.centerXProperty().bind(this.widthProperty().divide(2));
                    ellipse.centerYProperty().bind(this.heightProperty().divide(2));
                    ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));
                    ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));
                    ellipse.setStyle("-fx-stroke: inherit");
                    ellipse.setStrokeWidth(5);
                    ellipse.setFill(Color.TRANSPARENT);
                    oPane.getChildren().add(ellipse); // Add the ellipse to the pane

                    this.getChildren().add(oPane);
                }

                /**
                 * Display the given chip in a space in the default preview state.
                 * @param c the token to display, either X or O
                 */
                public void showToken(char c)
                {
                    if (c == 'X') {
                        oPane.setVisible(false);
                        xPane.setVisible(true);
                    } else if (c == 'O') {
                        oPane.setVisible(true);
                        xPane.setVisible(false);
                    }
                }

                /**
                 * Display no chip at all.
                 */
                public void hideToken() {
                    oPane.setVisible(false);
                    xPane.setVisible(false);
                }

                /**
                 * Make the given token visible and no longer grayed-out, the "permanent" styling of a completed move.
                 * @param c the char that should populate the cell
                 */
                public void setToken(char c) {
                    showToken(c);
                    if(c == 'X' || c == 'O') {
                        oPane.setOpacity(1.0);
                        xPane.setOpacity(1.0);
                    }
                }

                /**
                 * Highlight both shapes in red without changing their visibility or opacity.
                 */
                public void highlight() {
                    xPane.setStyle("-fx-stroke: red");
                    oPane.setStyle("-fx-stroke: red");
                }
            }
        }
    }
}
