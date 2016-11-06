package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
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
        launchGame(event);
    }
    public void  launchGame(ActionEvent event) {
        // Create the stage for the game and show it
        showGameStage();
        // Close the main menu
        closeMenuStage(event);

    }

    public void showGameStage() {
        try {
            p("Initializing Game Controller");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainMenuController.class.getResource("../views/Game.fxml"));
            AnchorPane anchorPane = loader.load();
            Stage gameStage = new Stage();
            gameStage.setTitle("Modify Parameters");
            Scene gameScene = new Scene(anchorPane);
            gameStage.initStyle(StageStyle.UTILITY);
            gameStage.setResizable(false);
            gameStage.setScene(gameScene);

            gameStage.setResizable(true);
            gameStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeMenuStage(ActionEvent event) {
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
    public void joinGameButtonHandler(ActionEvent event) {
        // Code for joining a game goes here
    }
    public void exitButtonHandler() {
        System.exit(0);
    }
    public void exitButtonHandler(ActionEvent event) {
        exitButtonHandler();
    }
    public void p(String s) {
        System.out.println(s);
    }
}
