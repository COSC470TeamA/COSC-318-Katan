package models;

import controllers.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by steve on 2016-11-10.
 */
public class ServerLog extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        start();
    }
    public void start() throws Exception {
//        try {
            System.out.println("Initializing Server Log");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainMenuController.class.getResource("../views/ServerLog.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage serverLogStage = new Stage();

            serverLogStage.setTitle("Server Log");
            Scene serverLogScene = new Scene(anchorPane);
            serverLogStage.initStyle(StageStyle.UTILITY);
            serverLogStage.setResizable(false);
            serverLogStage.setScene(serverLogScene);

            serverLogStage.setResizable(true);
            serverLogStage.setAlwaysOnTop(false);

            serverLogStage.show();

        System.out.println("Server Log Showing");
//        }
//        catch (IOException e) {
//
//        }
    }
}
