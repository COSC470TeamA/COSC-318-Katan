package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import client.GameClient;
import server.GameServer;
import server.GameServerThread;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

/**
 * Created by haunter on 31/10/16.
 */
public class MainMenuController implements Initializable {

    @FXML
    Button createGameButton, joinGameButton, exitButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createGameButton.setOnAction((event -> createGameButtonHandler(event)));
        joinGameButton.setOnAction((event -> joinGameButtonHandler(event)));
        exitButton.setOnAction((event -> exitButtonHandler()));
    }

    public void createGameButtonHandler(ActionEvent event) {
        // Code for creating a game goes here

        // Start a server thread for the game
        startServerConnection();
        // Start a client connection
        startClientConnection();
    }
    public void joinGameButtonHandler(ActionEvent event) {
        // Code for joining a game goes here
        startClientConnection();
    }

    public void showGameStage() {
        p("Initializing Game Controller");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainMenuController.class.getResource("../views/Game.fxml"));
        AnchorPane anchorPane = null;
        try {
            anchorPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage gameStage = new Stage();
        gameStage.setTitle("Modify Parameters");
        Scene gameScene = new Scene(anchorPane);
        gameStage.initStyle(StageStyle.UTILITY);
        gameStage.setResizable(false);
        gameStage.setScene(gameScene);

        gameStage.setResizable(true);
        gameStage.setAlwaysOnTop(false);
        gameStage.show();

    }

    public void closeMenuStage(ActionEvent event) {
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void exitButtonHandler() {
        System.exit(0);
    }
    public void exitButtonHandler(ActionEvent event) {
        exitButtonHandler();
    }

    public void startServerConnection() {
        String[] args = {};
        try {
            GameServer.main(args);

            new GameServerThread().start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startClientConnection() {
        // Start a client connection
        String[] args = {};
        try {
            GameClient.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void p(String s) {
        System.out.println(s);
    }
}
