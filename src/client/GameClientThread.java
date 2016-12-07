package client;

import controllers.GameController;
import controllers.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by steve on 2016-11-10.
 */
public class GameClientThread extends Application {
    Stage gameStage;
    GameController gameController;

    public GameClientThread() throws IOException {
        this("GameClientThread");
    }

    public GameClientThread(String name) throws IOException {
        super();
    }

    /**
     * Build a game stage and show it. Grabs a reference to the game stage's controller.
     * @param stageNOUSE
     * @throws Exception
     */
    @Override
    public void start(Stage stageNOUSE) throws Exception {

        try {
            System.out.println("Initializing Game Controller");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainMenuController.class.getResource("../views/Game.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gameController = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Katan");

            Scene gameScene = new Scene(anchorPane);

            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(gameScene);

            stage.setResizable(true);
            stage.setAlwaysOnTop(false);

            gameStage = stage;

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
