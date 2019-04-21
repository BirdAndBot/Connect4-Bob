package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Wrapper activity for the main game GUI. Sets up the stage and attaches a controller.
 */
public class Connect4GUIActivity extends Application {

    /**
     * Setup the activity with a layout and controller. Called by the threading/activity cycle.
     * @param primaryStage A JavaFX stage object into which to inject this activity
     * @throws Exception in the case of any configuration errors in the view or controller's startup
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gameView.fxml"));
        primaryStage.setTitle("Connect4");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * The entry point if this activity is run directly, rather than via the launcher.
     * @param args arguments passed at the command line
     */
    public static void main(String[] args) {
        launch(args);
    }
}
