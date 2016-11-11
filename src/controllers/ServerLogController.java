package controllers;

import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;


import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

/**
 * Created by haunter on 31/10/16.
 */
public class ServerLogController implements Initializable {

    public ServerLogController() {

    }

    @FXML
    public TextArea serverLogTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    public void printToLog(String string) {
        if (serverLogTextArea == null) {
            System.out.println("NULLLL");
        }
        else {


            serverLogTextArea.appendText(string + "\n");

        }
    }

}
