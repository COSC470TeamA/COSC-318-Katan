package controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.stage.*;
import models.Hex;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class GameController implements Initializable {

    @FXML
    Polygon test;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Hex hex = new Hex();
        hex.calculateVertices(test, 0, 0);
    }

    public void p(String s) {
        System.out.println(s);
    }
}
