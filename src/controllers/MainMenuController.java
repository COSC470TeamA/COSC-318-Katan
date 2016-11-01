package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by haunter on 31/10/16.
 */
public class MainMenuController {
    @FXML
    Button createGameButton, joinGameButton, exitButton;

    public void initialize() {
        createGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                System.out.println("PPPPPPPPPPP");
            }
        });
    }
}
