package models;

import controllers.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import server.GameServerThread;

import java.io.IOException;

/**
 * Created by steve on 2016-11-10.
 */
public class GameClient {
    public static void main(String[] args) throws IOException {
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
            Stage gameStage = new Stage();
            gameStage.setTitle("Katan");
            Scene gameScene = new Scene(anchorPane);
            gameStage.initStyle(StageStyle.UTILITY);
            gameStage.setResizable(false);
            gameStage.setScene(gameScene);

            gameStage.setResizable(true);
            gameStage.setAlwaysOnTop(false);

            new GameClientThread().start(gameStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
