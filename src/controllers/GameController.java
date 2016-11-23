package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import models.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;



public class GameController implements Initializable {
    /** The container for the game board */
    @FXML
    Pane boardPane;

    /** Circle used to highlight the mouseover of vertices. */
    @FXML
    Circle selectionCircle;

    /** The polygons used to represent game tiles. */
    @FXML
    Polygon          FXHex01, FXHex02, FXHex03,

                FXHex10, FXHex11, FXHex12, FXHex13,

            FXHex20, FXHex21, FXHex22, FXHex23, FXHex24,

                FXHex30, FXHex31, FXHex32, FXHex33,

                     FXHex41, FXHex42, FXHex43;

    @FXML
    Button rollDiceButton;

    @FXML
    public Label rollDiceLabel;

    /** A list for all the tiles (hexagon shapes) on the board */
    ArrayList<Polygon> allHexagons = new ArrayList<>(19);
    /** A list for all the tiles (resources) on the board */
    ArrayList<Tile> allTiles = new ArrayList<>(19);

    /** Object with tools for calculating and mutating hexagon points */
    Hex hex = new Hex();

    /** The hand of cards that belongs to this game */
    Hand hand = new Hand();

    /** The starting position of the hexes, relative to the upper left of their container */
    double BOARD_PADDING_X, BOARD_PADDING_Y = 0;

    /** The inital size of all edges of all tiles */
    double SIDE_LENGTH = 50;

    /** The radius of the selection circle */
    double SELECTION_CIRCLE_RADIUS = 10;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        createTiles();

        setTileResources();

        toServerTextField.setOnKeyPressed((event) -> handleToServerTextFieldKeyPressed(event));

        initializeButtons();

    }
    /**
     * Gets the stack of tile pieces to apply a Resource to each hexagon.
     */
    public void setTileResources() {
        ImagePattern lumberImagePattern = new ImagePattern(new Image("/assets/images/lumber.jpg"));
        ImagePattern grainImagePattern = new ImagePattern(new Image("/assets/images/grain.jpg"));
        ImagePattern oreImagePattern = new ImagePattern(new Image("/assets/images/ore.jpg"));
        ImagePattern woolImagePattern = new ImagePattern(new Image("/assets/images/wool.jpg"));
        ImagePattern brickImagePattern = new ImagePattern(new Image("/assets/images/brick.jpg"));
        ImagePattern desertImagePattern = new ImagePattern(new Image("/assets/images/desert.jpeg"));

        allTiles.forEach((tile) -> {
            Resource thisResource = tile.getResource();
            switch (thisResource) {
                case LUMBER:
                    tile.getHex().setFill(lumberImagePattern);
                    break;
                case WOOL:
                    tile.getHex().setFill(woolImagePattern);
                    break;
                case GRAIN:
                    tile.getHex().setFill(grainImagePattern);
                    break;
                case ORE:
                    tile.getHex().setFill(oreImagePattern);
                    break;
                case BRICK:
                    tile.getHex().setFill(brickImagePattern);
                    break;
                case DESERT:
                    tile.getHex().setFill(desertImagePattern);
                    break;
            }
        });
    }

    /**
     * Determines size of the game board.
     * Creates all game tile objects.
     * Attaches listeners to all game tiles.
     * Draws the tiles.
     */
    public void createTiles() {
        // Set the length of every side of the hex tiles (ie, set the board size)
        hex.setSideLength(SIDE_LENGTH);
        // Calculate the horizontal center of the board's container, offset for the board itself
        BOARD_PADDING_X = boardPane.getPrefWidth() / 2 - 4 * hex.getR();
        System.out.println(boardPane.getPrefWidth());

        RollMarkerStack rollMarkerStack = RollMarkerStack.getInstance();
        TileStack tileStack = TileStack.getInstance();

        selectionCircle.setVisible(false);
        selectionCircle.setRadius(SELECTION_CIRCLE_RADIUS);

        Iterator<Resource> tileStackIterator = tileStack.getIterator();
        Iterator<RollMarker> rollMarkerIterator = rollMarkerStack.getIterator();

        boardPane.getChildren().forEach((hex) -> {

            if (hex.getId().startsWith("FXHex")) {
                Polygon thisHex = (Polygon) hex;
                allHexagons.add(thisHex);
                HexagonCoordinate coord = getCoords(thisHex);

                Resource nextResource = tileStackIterator.next();
                Tile tile;

                if (nextResource.equals(Resource.DESERT)) {
                    // Don't pop a marker if resource is desert
                    // There are no markers on the desert
                    tile = new Tile(coord, thisHex, nextResource, null);
                }
                else {
                    tile = new Tile(coord, thisHex, nextResource, rollMarkerIterator.next());
                }
                allTiles.add(tile);
            }

        });

        allHexagons.forEach((hex) -> {
            hex.setLayoutY(BOARD_PADDING_Y);
            hex.setLayoutX(BOARD_PADDING_X);
            hex.setOnMouseClicked((event) -> handleTileClick(event));
            hex.setOnMouseEntered((event) -> handleMouseEnter(event));
            hex.setOnMouseExited((event) -> handleMouseExit(event));
            hex.setOnMouseMoved((event) -> handleMouseMove(event));
        });
        selectionCircle.setLayoutX(BOARD_PADDING_X);
        selectionCircle.setLayoutY(BOARD_PADDING_Y);
        selectionCircle.setOnMouseExited((event) -> handleSelectionCircleMouseExit(event));
        selectionCircle.setOnMouseClicked((event) -> handleSelectionCircleMouseClicked(event));

        drawHexGrid();
    }

    /**
     * Invoked on mouse click on one of the game tiles.
     * @param event
     */
    public void handleTileClick(MouseEvent event) {
        Polygon clickedTile = (Polygon) event.getSource();
        System.out.println("Clicked " + clickedTile.toString());
        System.out.println("at " + getCoords(clickedTile).getX() + ", " + getCoords(clickedTile).getY());
        System.out.println(hexToTile(clickedTile).getResource().toString());
        p(getCoords(clickedTile).toString() + " Marker = " + hexToTile(clickedTile).getRollMarker().getRoll());
    }

    /**
     * Invoked on mouse move in a game tile.
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleMouseMove(MouseEvent event) {
        Polygon thisTile = (Polygon) event.getSource();
        ObservableList<Double> points = thisTile.getPoints();
        ArrayList<Coordinate> tileVertices = new ArrayList<>(6);
        ArrayList<Coordinate> tileMidPoints = new ArrayList<>(6);
        // Put each vertex (pair of points) in the list
        for (int i = 0; i < 12; i += 2) {
            tileVertices.add(new Coordinate(points.get(i), points.get(i + 1)));
        }
        // Put each midpoint of each side in the list
        for (int i = 0; i < 12; i += 2) {
            tileMidPoints.add(new Coordinate(points.get(i), points.get(i + 1)).midpoint(
                    new Coordinate(points.get((i + 2) % 12), points.get((i + 3) % 12))));
        }
        // Add the lists so we can iterate once only
        tileVertices.addAll(tileMidPoints);
        Coordinate currMouseCoord = new Coordinate(event.getX(), event.getY());
        for (Coordinate vertex : tileVertices) {
            if (currMouseCoord.isCloseTo(vertex)) {
                selectionCircle.setCenterX(vertex.getX());
                selectionCircle.setCenterY(vertex.getY());
                selectionCircle.setVisible(true);
                break;
            }
        }

    }

    /**
     * Invoked on mouse enter of a game tile.
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleMouseEnter(MouseEvent event) {
        Polygon thisTile = (Polygon) event.getSource();
        selectionCircle.setVisible(false);
    }

    /**
     * Invoked on mouse exit of a game tile.
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleMouseExit(MouseEvent event) {
        Polygon thisTile = (Polygon) event.getSource();
        //thisTile.setFill(Color.DODGERBLUE);
    }

    /**
     * Invoked on mouse exit of the selection circle.
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleSelectionCircleMouseExit(MouseEvent event) {
        selectionCircle.setVisible(false);
    }

    /**
     * Invoked on mouse click of the selection circle.
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleSelectionCircleMouseClicked(MouseEvent event) {

    }

    /**
     * Send each polygon (hex) to the Hex tools class to have its
     * set of points mutated.
     * Each has has 6 vertices with with 2 points each, so 12 points.
     *
     * Refer to Hex.java for documentation on point mutation.
     */
    public void drawHexGrid() {


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

    /**
     * Takes a hexagon and returns the Tile based on the logical coordinates.
     *
     * @param hex
     * @return
     */
    public Tile hexToTile(Polygon hex) {
        HexagonCoordinate logicalCoords = getCoords(hex);
        int row = logicalCoords.getX();
        int col = logicalCoords.getY();
        int arrayIndex = 0;
        switch (row) {
            case 0:
                arrayIndex = 0;
                break;
            case 1:
                arrayIndex = 3;
                break;
            case 2:
                arrayIndex = 7;
                break;
            case 3:
                arrayIndex = 12;
                break;
            case 4:
                arrayIndex = 16;
                break;
        }
        // The first and last rows do not have a 0th hexagon
        if (row == 0 || row == 4) {
            col --;
        }
        arrayIndex += col;

        return allTiles.get(arrayIndex);
    }

    private void initializeButtons() {
        rollDiceButton.setOnMouseClicked((event) -> handleDiceRollMouseClick(event));
    }
private void handleDiceRollMouseClick(MouseEvent event) {
    sendMessageToServer("rd");
}

    /**
     * Sends a message to the server thread.
     *
     * @param message
     */
    private void sendMessageToServer(String message) {
    if (toServerLabel.getText().equals(message)) {
        toServerLabel.setText("");
    }
    toServerLabel.setText(message);
}
    /**
     * Label to put server bound messages in.
     * The messages are automatically grabbed by the game client thread
     * and forwarded to the server to be interpreted.
     */
    @FXML
    Label toServerLabel;
    @FXML
    TextField toServerTextField;
    public void p(String s) {
        toServerLabel.setText(s);
    }
    public Label getToServerLabel() {
        return toServerLabel;
    }
    public Label getRollDiceLabel() { return rollDiceLabel; }

    /**
     * When the Enter key is pressed, sets the toServerLabel text
     * to be the text in this Text Field.
     * @param event
     */
    public void handleToServerTextFieldKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            toServerLabel.setText(toServerTextField.getText());
        }

    }
}
