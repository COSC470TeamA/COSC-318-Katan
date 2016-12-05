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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import models.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import static models.Resource.LUMBER;
import static models.Resource.WOOL;


public class GameController implements Initializable {
    /** The container for the game board */
    @FXML
    Pane boardPane;

    /** Circle used to highlight the mouseover of vertices. */
    @FXML
    Circle selectionCircle, selectionCircle2;

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
    Label rollDiceLabel;

    @FXML
    Rectangle roadRectangle;

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
    double ROLL_MARKER_CIRCLE_RADIUS = 20;

    double ROAD_RECTABGLE_HEIGHT = 42;
    double ROAD_RECTABGLE_WIDTH = 8;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        createTiles();

        setTileResources();

        setTileRoleMarkers();

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
     * Gets the stack of tile pieces to apply a roll marker to each hexagon.
     */
    public void setTileRoleMarkers() {
        allTiles.forEach((tile) -> {
            if (!tile.getResource().name().equals("DESERT")) {
                RollMarker thisRollMarker = tile.getRollMarker();

                ArrayList<Coordinate> tileCoords = new ArrayList<>();

                for (int i = 0; i < tile.getHex().getPoints().size() - 1; i++) {
                    //sets of coords to calc centre of tile
                    tileCoords.add(new Coordinate(tile.getHex().getPoints().get(i), tile.getHex().getPoints().get(i + 1)));
                }

                StackPane stackPane = new StackPane();

                Circle markerCircle = new Circle();
                markerCircle.setRadius(ROLL_MARKER_CIRCLE_RADIUS);
                markerCircle.setFill(Color.WHITE);
                markerCircle.setStroke(Color.BLACK);


                Label markerLabel = new Label();
                markerLabel.setText(String.valueOf(thisRollMarker.getRoll()));
                stackPane.getChildren().addAll(markerCircle, markerLabel);
                stackPane.setTranslateX(tileCoords.get(0).getX() - (SIDE_LENGTH/2) + (ROLL_MARKER_CIRCLE_RADIUS / 4));
                stackPane.setTranslateY(tileCoords.get(0).getY() + (SIDE_LENGTH/2));
                stackPane.setLayoutX(BOARD_PADDING_X);
                stackPane.setLayoutY(BOARD_PADDING_Y);
                boardPane.getChildren().addAll(stackPane);
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
        selectionCircle2.setVisible(false);
        roadRectangle.setVisible(false);
        selectionCircle.setRadius(SELECTION_CIRCLE_RADIUS);
        selectionCircle2.setRadius(SELECTION_CIRCLE_RADIUS);
        roadRectangle.setHeight(ROAD_RECTABGLE_HEIGHT);
        roadRectangle.setWidth(ROAD_RECTABGLE_WIDTH);

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
        selectionCircle2.setLayoutX(BOARD_PADDING_X);
        selectionCircle2.setLayoutY(BOARD_PADDING_Y);
        roadRectangle.setLayoutX(BOARD_PADDING_X);
        roadRectangle.setLayoutY(BOARD_PADDING_Y);
        selectionCircle.setOnMouseExited((event) -> handleSelectionCircleMouseExit(event));
        selectionCircle.setOnMouseClicked((event) -> handleSelectionCircleMouseClicked(event));
        selectionCircle2.setOnMouseExited((event) -> handleSelectionCircle2MouseExit(event));
        selectionCircle2.setOnMouseClicked((event) -> handleSelectionCircle2MouseClicked(event));
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
        // Not necessary to send this data to the server now
        //p(getCoords(clickedTile).toString() + " Marker = " + hexToTile(clickedTile).getRollMarker().getRoll());
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

        Coordinate currMouseCoord = new Coordinate(event.getX(), event.getY());
        for (Coordinate vertex : tileVertices) {
            if (currMouseCoord.isCloseTo(vertex)) {
                showSelectionCircle(vertex);
                break;
            }
        }

        // @TODO wtf repeat much
        Iterator<Coordinate> it = tileMidPoints.iterator();
        Coordinate vertex = it.next();
        if (currMouseCoord.isCloseTo(vertex)) showSelectionCircle(vertex, true, 120);
        vertex = it.next();
        if (currMouseCoord.isCloseTo(vertex)) showSelectionCircle(vertex, true, 0);
        vertex = it.next();
        if (currMouseCoord.isCloseTo(vertex)) showSelectionCircle(vertex, true, 60);
        vertex = it.next();
        if (currMouseCoord.isCloseTo(vertex)) showSelectionCircle(vertex, true, 120);
        vertex = it.next();
        if (currMouseCoord.isCloseTo(vertex)) showSelectionCircle(vertex, true, 0);
        vertex = it.next();
        if (currMouseCoord.isCloseTo(vertex)) showSelectionCircle(vertex, true, 60);

    }

    public void showSelectionCircle(Coordinate coord) {
        selectionCircle.setCenterX(coord.getX());
        selectionCircle.setCenterY(coord.getY());
        selectionCircle.setVisible(true);
    }

    public void showSelectionCircle(Coordinate coord, boolean isSide, int degrees) {
        if (isSide) {
            selectionCircle2.setCenterX(coord.getX());
            selectionCircle2.setCenterY(coord.getY());
            selectionCircle2.setRotate(degrees);
            selectionCircle2.setVisible(true);
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
    }

    /**
     * Invoked on mouse exit of the selection circle.
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleSelectionCircleMouseExit(MouseEvent event) {
        selectionCircle.setVisible(false);
    }
    public void handleSelectionCircle2MouseExit(MouseEvent event) {
        selectionCircle2.setVisible(false);
    }
    /**
     * Invoked on mouse click of the side selection circle.
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleSelectionCircle2MouseClicked(MouseEvent event) {
//        roadRectangle.setX(event.getX() - ROAD_RECTABGLE_WIDTH / 2);
//        roadRectangle.setY(event.getY() - SIDE_LENGTH / 2);
//        roadRectangle.setVisible(true);
        sendMessageToServer("dr:" + event.getX() + ":" + event.getY());
    }

    public void handleSelectionCircleMouseClicked(MouseEvent event) {
        sendMessageToServer("dh:" + event.getX() + ":" + event.getY());
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

    public void drawRollMarkers() {

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

    /**
     * Draws a house shaped polygon on the board at the coordiantes
     * specified in the message received.
     * @param s The message.
     */
    public void drawHouse(String s) {
        String[] messageArray = s.split(":");
        Double eventX = Double.parseDouble(messageArray[1]);
        Double eventY = Double.parseDouble(messageArray[2]);
        String colorValue = messageArray[3];

        Polygon polygon = new Polygon();
        polygon.setFill(Color.valueOf(colorValue));
        polygon.setStroke(Color.BLACK);
        polygon.setLayoutX(BOARD_PADDING_X);
        polygon.setLayoutY(BOARD_PADDING_Y);

        polygon.getPoints().addAll(
                eventX , eventY - 15.0,
                eventX  - 10.0, eventY - 5.0,
                eventX  - 10.0, eventY + 10.0,
                eventX  + 10.0, eventY + 10.0,
                eventX  + 10.0, eventY - 5.0
        );

        boardPane.getChildren().addAll(polygon);
    }

    public void drawRoad(String message) {
        String[] messageArray = message.split(":");
        Double eventX = Double.parseDouble(messageArray[1]);
        Double eventY = Double.parseDouble(messageArray[2]);
        String colorValue = messageArray[3];

        Rectangle road = new Rectangle();
        road.setHeight(ROAD_RECTABGLE_HEIGHT);
        road.setWidth(ROAD_RECTABGLE_WIDTH);
        road.setLayoutX(BOARD_PADDING_X);
        road.setLayoutY(BOARD_PADDING_Y);
        road.setX(eventX - ROAD_RECTABGLE_WIDTH / 2);
        road.setY(eventY - SIDE_LENGTH / 2);
        road.setFill(Color.valueOf(colorValue));
        road.setStroke(Color.BLACK);
        // Angles are 0, 60, 120, 180, 240, 300
        road.setRotate(selectionCircle2.getRotate());

        boardPane.getChildren().addAll(road);

    }
}
