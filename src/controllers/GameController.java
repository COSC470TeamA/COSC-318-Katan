package controllers;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.*;
import models.Coordinate;
import models.Hex;
import models.HexagonCoordinate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;


public class GameController implements Initializable {
    @FXML
    Pane boardPane;

    @FXML
    Circle selectionCircle;

    @FXML
    Polygon          FXHex01, FXHex02, FXHex03,
                FXHex10, FXHex11, FXHex12, FXHex13,
            FXHex20, FXHex21, FXHex22, FXHex23, FXHex24,
                FXHex30, FXHex31, FXHex32, FXHex33,
                     FXHex41, FXHex42, FXHex43;

    /** A list for all the tiles (hexagon shapes) on the board */
    ArrayList<Polygon> allTiles = new ArrayList<>(19);

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        drawHexGrid();
        selectionCircle.setVisible(false);
        selectionCircle.setRadius(10);
        boardPane.getChildren().forEach((hex) -> {
            if (hex.getId().startsWith("FXHex"))
                allTiles.add((Polygon) hex);
        });

        allTiles.forEach((hex) -> {
            hex.setOnMouseClicked((event) -> handleTileClick(event));
            hex.setOnMouseEntered((event) -> handleMouseEnter(event));
            hex.setOnMouseExited((event) -> handleMouseExit(event));
            hex.setOnMouseMoved((event) -> handleMouseMove(event));
        });
        selectionCircle.setOnMouseExited((event) -> handleSelectionCircleMouseExit(event));

    }

    /**
     * Invoked on mouse click on one of the game tiles.
     * @param event
     */
    public void handleTileClick(MouseEvent event) {
        Polygon clickedTile = (Polygon) event.getSource();
        System.out.println("Clicked " + clickedTile.toString());
        p(getCoords(clickedTile).toString());
    }

    /**
     * Invoked on mouse move in a game tile.
     * @param event
     */
    public void handleMouseMove(MouseEvent event) {
        Polygon thisTile = (Polygon) event.getSource();
        ObservableList<Double> points = thisTile.getPoints();
        ArrayList<Coordinate> tileVertices = new ArrayList<>(6);
        for (int i = 0; i < 12; i ++) {
            tileVertices.add(new Coordinate(points.get(i++), points.get(i)));
        }
        Coordinate currMouseCoord = new Coordinate(event.getX(), event.getY());
        for (Coordinate vertex : tileVertices) {
            if (currMouseCoord.isCloseTo(vertex)) {
                selectionCircle.setCenterX(vertex.getX() - Hex.getSideLength());
                selectionCircle.setCenterY(vertex.getY());
                selectionCircle.setVisible(true);
            }
        }

    }

    /**
     * Invoke on mouse enter of a game tile.
     * @param event
     */
    public void handleMouseEnter(MouseEvent event) {
        Polygon thisTile = (Polygon) event.getSource();
        thisTile.setFill(Color.LIGHTBLUE);
        selectionCircle.setVisible(false);
    }
    public void handleMouseExit(MouseEvent event) {
        Polygon thisTile = (Polygon) event.getSource();
        thisTile.setFill(Color.DODGERBLUE);
    }
    public void handleSelectionCircleMouseExit(MouseEvent event) {
        selectionCircle.setVisible(false);
    }

    public void drawHexGrid() {

        Hex hex = new Hex();
        // Top row
        hex.calculateVertices(FXHex01, new HexagonCoordinate(0, 1));
        hex.calculateVertices(FXHex02, new HexagonCoordinate(0, 2));
        hex.calculateVertices(FXHex03, new HexagonCoordinate(0, 3));
        // Second row, positively offset
        hex.calculateVertices(FXHex10, new HexagonCoordinate(1, 0));
        hex.calculateVertices(FXHex11, new HexagonCoordinate(1, 1));
        hex.calculateVertices(FXHex12, new HexagonCoordinate(1, 2));
        hex.calculateVertices(FXHex13, new HexagonCoordinate(1, 3));
        // Third row
        hex.calculateVertices(FXHex20, new HexagonCoordinate(2, 0));
        hex.calculateVertices(FXHex21, new HexagonCoordinate(2, 1));
        hex.calculateVertices(FXHex22, new HexagonCoordinate(2, 2));
        hex.calculateVertices(FXHex23, new HexagonCoordinate(2, 3));
        hex.calculateVertices(FXHex24, new HexagonCoordinate(2, 4));
        // Fourth row, positively offset
        hex.calculateVertices(FXHex30, new HexagonCoordinate(3, 0));
        hex.calculateVertices(FXHex31, new HexagonCoordinate(3, 1));
        hex.calculateVertices(FXHex32, new HexagonCoordinate(3, 2));
        hex.calculateVertices(FXHex33, new HexagonCoordinate(3, 3));
        // Fifth row
        hex.calculateVertices(FXHex41, new HexagonCoordinate(4, 1));
        hex.calculateVertices(FXHex42, new HexagonCoordinate(4, 2));
        hex.calculateVertices(FXHex43, new HexagonCoordinate(4, 3));
    }

    public HexagonCoordinate getCoords(Polygon hex) {
        // All Polygons follow the naming convention FXHex + row + col
        String id = hex.getId();
        int row = Integer.valueOf(id.substring(5, 6));
        int col = Integer.valueOf(id.substring(6, 7));
        return new HexagonCoordinate(row, col);
    }

    public static void p(String s) {
        System.out.println(s);
    }
}
