package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import socketfx.Constants;
import socketfx.FxSocketClient;
import socketfx.SocketListener;

import java.awt.*;
import java.net.URL;
import java.util.*;

public class GameController implements Initializable {


    /**
     * The container for the game board
     */
    @FXML
    Pane boardPane;

    /**
     * Circle used to highlight the mouseover of vertices.
     */
    @FXML
    Circle selectionCircle, selectionCircle2;

    /**
     * The polygons used to represent game tiles.
     */
    @FXML
    Polygon FXHex01, FXHex02, FXHex03,

    FXHex10, FXHex11, FXHex12, FXHex13,

    FXHex20, FXHex21, FXHex22, FXHex23, FXHex24,

    FXHex30, FXHex31, FXHex32, FXHex33,

    FXHex41, FXHex42, FXHex43;

    @FXML
    Button rollDiceButton, startGameButton, endTurnButton, buildHouseButton, buildRoadButton;

    @FXML
    Label rollDiceLabel, turnLabel;

    @FXML
    Rectangle roadRectangle;

    /**
     * Label to put server bound messages in.
     * The messages are automatically grabbed by the game client thread
     * and forwarded to the server to be interpreted.
     */
    @FXML
    Label toServerLabel;
    @FXML
    TextField toServerTextField;

    @FXML
    Label lumberCountLabel, woolCountLabel, brickCountLabel, grainCountLabel, oreCountLabel;

    @FXML
    Label playerLabel;

    @FXML
    Label gameWinLabel;

    @FXML
    ImageView costs;

    @FXML
            Image costsImage;

    /**
     * A list for all the tiles (hexagon shapes) on the board
     */
    ArrayList<Polygon> allHexagons = new ArrayList<>(19);
    /**
     * A list for all the tiles (resources) on the board
     */
    ArrayList<Tile> allTiles = new ArrayList<>(19);

    /**
     * Object with tools for calculating and mutating hexagon points
     */
    Hex hex = new Hex();

    /**
     * The hand of cards that belongs to this game
     */
    Hand hand = new Hand();


    /**
     * The starting position of the hexes, relative to the upper left of their container
     */
    double BOARD_PADDING_X, BOARD_PADDING_Y = 0;

    /**
     * The inital size of all edges of all tiles
     */
    double SIDE_LENGTH = 50;

    /**
     * The radius of the selection circle
     */
    double SELECTION_CIRCLE_RADIUS = 10;
    double ROLL_MARKER_CIRCLE_RADIUS = 20;

    double ROAD_RECTABGLE_HEIGHT = 44;
    double ROAD_RECTABGLE_WIDTH = 7;

    private FxSocketClient socket;

    private boolean connected;
    private volatile boolean isAutoConnected;

    private static final int DEFAULT_RETRY_INTERVAL = 2000; // in milliseconds

    private boolean isMyTurn = false;

    private boolean gameOver = false;

    private boolean buildingAHouse = false, buildingARoad = false;

    /*
     * Synchronized method set up to wait until there is no socket connection.
     * When notifyDisconnected() is called, waiting will cease.
     */
    private synchronized void waitForDisconnect() {
        while (connected) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /*
     * Synchronized method responsible for notifying waitForDisconnect()
     * method that it's OK to stop waiting.
     */
    private synchronized void notifyDisconnected() {
        connected = false;
        notifyAll();
    }

    /*
     * Synchronized method to set isConnected boolean
     */
    private synchronized void setIsConnected(boolean connected) {
        this.connected = connected;
    }

    /*
     * Synchronized method to check for value of connected boolean
     */
    private synchronized boolean isConnected() {
        return (connected);
    }

    private void connect() {
        socket = new FxSocketClient(new GameController.FxSocketListener(),
                "localhost",
                4445,
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }

    private void autoConnect() {
        new Thread() {
            @Override
            public void run() {
                while (isAutoConnected) {
                    if (!isConnected()) {
                        socket = new FxSocketClient(new GameController.FxSocketListener(),
                                "localhost",
                                4445,
                                Constants.instance().DEBUG_NONE);
                        socket.connect();
                    }
                    waitForDisconnect();
                    try {
                        Thread.sleep(DEFAULT_RETRY_INTERVAL * 1000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        createTiles();

        setTileResources();

        setTileRoleMarkers();

        toServerTextField.setOnKeyPressed((event) -> handleToServerTextFieldKeyPressed(event));

        initializeButtons();

        connect();
    }

    private void setIntitalResources() {
        for (Resource resource : Resource.values()) {
            Card card = new Card();
            card.setResource(resource);
            hand.addCard(card);
        }
        updateHandText();
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
                stackPane.setTranslateX(tileCoords.get(0).getX() - (SIDE_LENGTH / 2) + (ROLL_MARKER_CIRCLE_RADIUS / 4));
                stackPane.setTranslateY(tileCoords.get(0).getY() + (SIDE_LENGTH / 2));
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
                } else {
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
     *
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
     *
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
        for (Coordinate vertex : tileMidPoints) {
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

    public void showSelectionCircle(Coordinate coord, boolean isSide) {
        if (isSide) {
            selectionCircle2.setCenterX(coord.getX());
            selectionCircle2.setCenterY(coord.getY());
            selectionCircle2.setVisible(true);
        }
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
     *
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleMouseEnter(MouseEvent event) {
        Polygon thisTile = (Polygon) event.getSource();
        selectionCircle.setVisible(false);
    }

    /**
     * Invoked on mouse exit of a game tile.
     *
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleMouseExit(MouseEvent event) {
        Polygon thisTile = (Polygon) event.getSource();
    }

    /**
     * Invoked on mouse exit of the selection circle.
     *
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
     *
     * @param event The Mouse Event which invoked this listener.
     */
    public void handleSelectionCircle2MouseClicked(MouseEvent event) {
        if (buildingARoad) {
            sendMessageToServer("dr:" + event.getX() + ":" + event.getY() + ":" + selectionCircle2.getRotate());
            // Remove the right cards from the player
            hand.removeRoadCards();
        }
        buildingARoad = false;
        // Reset the builder buttons based on what can be afforded
        refreshAfterBuilding();
    }

    public void handleSelectionCircleMouseClicked(MouseEvent event) {
        if (buildingAHouse) {
            buildHouseAt(event.getX(), event.getY());
            // Remove the right cards from the player
            hand.removeHouseCards();
        }
        buildingAHouse = false;
        // Reset the builder buttons based on what can be afforded
        refreshAfterBuilding();
    }

    public void buildHouseAt(double x, double y) {
        //get tiles adjacent to house and send with message
        ArrayList<Tile> surroundingTiles = getTilesSurrounding(x, y);
        String surroundingTilesMessage = "";
        for (Tile tile : surroundingTiles) {
            RollMarker rollMarker = getRollMarker(tile.getLogicalCoordinate());
            Resource resource = getResource(tile.getLogicalCoordinate());

            if (resource != Resource.DESERT) {
                surroundingTilesMessage += "!" + tile.getLogicalCoordinate().toString() + "^" + rollMarker.getRoll() + "," + resource.name();
            }
        }
        sendMessageToServer("dh:" + x + ":" + y + ":" + surroundingTilesMessage);
    }

    public void buildRoadAt(double x, double y) {
        sendMessageToServer("dr:" + x + ":" + y + ":" + selectionCircle2.getRotate());
    }

    public void buildVerticalRoadAt(double x, double y) {
        sendMessageToServer("dr:" + x + ":" + y + ":" + 0);
    }

    public void buildUpRoadAt(double x, double y) {
        sendMessageToServer("dr:" + x + ":" + y + ":" + 60);
    }

    public void buildDownRoadAt(double x, double y) {
        sendMessageToServer("dr:" + x + ":" + y + ":" + 120);
    }

    private Resource getResource(HexagonCoordinate logicalCoordinate) {
        for (Tile tile : allTiles) {
            if (tile.getLogicalCoordinate() == logicalCoordinate) {
                return tile.getResource();
            }
        }
        return null;
    }

    private RollMarker getRollMarker(HexagonCoordinate logicalCoordinate) {
        for (Tile tile : allTiles) {
            if (tile.getLogicalCoordinate() == logicalCoordinate) {
                return tile.getRollMarker();
            }
        }
        return null;
    }

    private ArrayList<Tile> getTilesSurrounding(double x, double y) {
        ArrayList<Tile> surroundingTiles = new ArrayList<>();

        //make a map containing all coordinates for each tile in the game
        Map<Tile, ArrayList<Coordinate>> allTilesList = new HashMap<>();
        for (Tile tile : allTiles) {
            ObservableList<Double> points = tile.getHex().getPoints();
            ArrayList<Coordinate> tileVertices = new ArrayList<>(6);

            // Put each vertex (pair of points) in the list
            for (int i = 0; i < 12; i += 2) {
                tileVertices.add(new Coordinate(points.get(i), points.get(i + 1)));
            }
            allTilesList.put(tile, tileVertices);
        }

        //check every tiles vertices to determine if house is being placed close to it
        Coordinate currMouseCoord = new Coordinate(x, y);
        for (Tile tile : allTilesList.keySet()) {
            for (Coordinate coord : allTilesList.get(tile)) {
                if (currMouseCoord.isCloseTo(coord)) {
                    surroundingTiles.add(tile);
                }
            }
        }

        return surroundingTiles;
    }

    /**
     * Send each polygon (hex) to the Hex tools class to have its
     * set of points mutated.
     * Each has has 6 vertices with with 2 points each, so 12 points.
     * <p>
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
            col--;
        }
        arrayIndex += col;

        return allTiles.get(arrayIndex);
    }

    private void initializeButtons() {
        endTurnButton.setDisable(true);
        rollDiceButton.setDisable(true);
        buildHouseButton.setDisable(true); // Enabled for testing
        buildRoadButton.setDisable(true);
        rollDiceButton.setOnMouseClicked(this::handleDiceRollMouseClick);
        startGameButton.setOnMouseClicked(this::handleStartGameButton);
        endTurnButton.setOnMouseClicked(this::handleEndTurnButton);
        buildHouseButton.setOnMouseClicked(this::handleBuildHouseButtonClick);
        buildRoadButton.setOnMouseClicked(this::handleBuildRoadButtonClick);

        toServerTextField.setVisible(false);
        toServerLabel.setVisible(false);

        boardPane.setOnMouseClicked(this::handleMouseClickBoardPane);

        costs.toFront();
//        costs.setLayoutX(20);
//        costs.setLayoutY(20);
//        costs.setX(20);
//        costs.setY(20);
    }

    private void handleMouseClickBoardPane(MouseEvent event) {
        if (buildingAHouse) {
            buildingAHouse = false;
            buildHouseButton.setDisable(false);
        }
        else if (buildingARoad) {
            buildingARoad = false;
            buildRoadButton.setDisable(false);
        }
    }

    private void handleDiceRollMouseClick(MouseEvent event) {
        // Turn off the roll dice button
        rollDiceButton.setDisable(true);
        // Turn on the end turn button
        endTurnButton.setDisable(false);
        sendMessageToServer("rd");
    }

    private void handleStartGameButton(MouseEvent event) {
        initializeStartingPlayerHousesAndRoads();
        sendMessageToServer("sg");
        // Turn off the start game button for the rest of the game
        startGameButton.setDisable(true);
        startTurn();
    }

    int sleepAmount = 500;

    private void initializeStartingPlayerHousesAndRoads() {
        try {
            buildUpRoadAt(107.29, 88.15);
            Thread.sleep(sleepAmount);
            buildVerticalRoadAt(259.20, 275.0);
            Thread.sleep(sleepAmount);
            buildHouseAt(130.20, 75.0);
            Thread.sleep(sleepAmount);
            buildHouseAt(260.20, 249.0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeSecondPlayerBuildings() {
        try {
            buildDownRoadAt(65.25, 237.03);
            Thread.sleep(sleepAmount);
            buildDownRoadAt(194.24, 162.44);
            Thread.sleep(sleepAmount);
            buildHouseAt(176.20, 149.0);
            Thread.sleep(sleepAmount);
            buildHouseAt(88.20, 248.0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleEndTurnButton(MouseEvent event) {

        sendMessageToServer("et");

        turnLabel.setText("Waiting for players");

        // Turn off the roll dice button
        rollDiceButton.setDisable(true);
        // Turn off the end turn button
        endTurnButton.setDisable(true);
        // Turn off the builder buttons
        buildRoadButton.setDisable(true);
        buildHouseButton.setDisable(true);
        // Make sure they can't build out of turn
        buildingARoad = false;
        buildingAHouse = false;
    }

    private void handleBuildHouseButtonClick(MouseEvent event) {
        System.out.println(hand.canAffordHouse());
        buildHouseButton.setDisable(true);


        // Start the building a house phase
        buildingAHouse = true;
    }

    private void handleBuildRoadButtonClick(MouseEvent event) {
        System.out.println(hand.canAffordRoad());
        buildRoadButton.setDisable(true);

        // Start the building a road phase
        buildingARoad = true;
    }

    private void refreshAfterBuilding() {
        enableAffordableBuilderButtons();
        updateHandText();
    }

    private void enableAffordableBuilderButtons() {
        buildHouseButton.setDisable(!hand.canAffordHouse());
        buildRoadButton.setDisable(!hand.canAffordRoad());
    }

    public void startTurn() {
        System.out.println("STARTING TURN");
        isMyTurn = true;
        // Set the label to describe who's turn it is
        turnLabel.setText("Your turn");

        // Turn on the Roll Dice button
        rollDiceButton.setDisable(false);
    }

    public Label getToServerLabel() {
        return toServerLabel;
    }

    public Label getRollDiceLabel() {
        return rollDiceLabel;
    }

    /**
     * When the Enter key is pressed, sets the toServerLabel text
     * to be the text in this Text Field.
     *
     * @param event
     */
    public void handleToServerTextFieldKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            sendMessageToServer(toServerTextField.getText());
        }
    }

    public void drawHouse(String s) {
        String[] messageArray = s.split(":");
        Double eventX = Double.parseDouble(messageArray[1]);
        Double eventY = Double.parseDouble(messageArray[2]);
        String colorValue = messageArray[4];

        Polygon polygon = new Polygon();
        polygon.setFill(Color.valueOf(colorValue));
        polygon.setStroke(Color.BLACK);
        polygon.setLayoutX(BOARD_PADDING_X);
        polygon.setLayoutY(BOARD_PADDING_Y);

        polygon.getPoints().addAll(
                eventX, eventY - 15.0,
                eventX - 10.0, eventY - 5.0,
                eventX - 10.0, eventY + 10.0,
                eventX + 10.0, eventY + 10.0,
                eventX + 10.0, eventY - 5.0
        );
        boardPane.getChildren().addAll(polygon);
    }

    public void drawRoad(String message) {
        String[] messageArray = message.split(":");
        Double eventX = Double.parseDouble(messageArray[1]);
        Double eventY = Double.parseDouble(messageArray[2]);
        Double rotate = Double.parseDouble(messageArray[3]);
        String colorValue = messageArray[4];

        Rectangle road = new Rectangle();
        road.setHeight(ROAD_RECTABGLE_HEIGHT);
        road.setWidth(ROAD_RECTABGLE_WIDTH);
        road.setLayoutX(BOARD_PADDING_X);
        road.setLayoutY(BOARD_PADDING_Y);
        road.setX(eventX - ROAD_RECTABGLE_WIDTH / 2);
        road.setY(eventY - SIDE_LENGTH / 2);
        road.setFill(Color.valueOf(colorValue));
        road.setStroke(Color.BLACK);
        // Angles are 120, 180, 240, 300, 0, 60
        road.setRotate(rotate);

        boardPane.getChildren().addAll(road);
    }

    private void initializeGame(String receivedMessage) {
        startGameButton.setDisable(true);
        startGameButton.setVisible(false);
        if (!isMyTurn) {
            // Initialize starting houses for person who
            // did not hit the start game button
            initializeSecondPlayerBuildings();
        }
        setPlayerColor(receivedMessage);
        setIntitalResources();
    }

    private void setPlayerColor(String receivedMessage) {
        String[] message = receivedMessage.split(":");
        playerLabel.setTextFill(Color.valueOf(message[1]));
    }

    private void sendMessageToServer(String message) {
        if (toServerLabel.getText().equals(message)) {
            toServerLabel.setText("");
        }
        toServerLabel.setText(message);
        sendRequest(message);
    }

    public void sendRequest(String message) {
        socket.sendMessage(message);
    }

    public void handleMessage(String receivedMessage) {
        String dString;

        String[] receivedMessageArray = receivedMessage.split(":");
        switch (receivedMessageArray[0]) {
            case "ir":
                incrementResource(receivedMessage);
                // If it's your turn, after getting resources check for building
                if (isMyTurn) enableAffordableBuilderButtons();
                break;
            case "rd":
                //set dice rolled label
                getRollDiceLabel().setText(receivedMessageArray[1]);
                // If it's your turn, after the roll dice check for building
                if (isMyTurn) enableAffordableBuilderButtons();
                break;
            case "dr":
                //Add house to game board
                drawRoad(receivedMessage);
                break;
            case "dh":
                //Add house to game board
                drawHouse(receivedMessage);
                break;
            case "sg":
                //start game
                initializeGame(receivedMessage);
                break;
            case "vr":
                setWin(true);
                break;
            case "gw":
                setWin(false);
                break;
            case "et":
                if (isMyTurn)
                    isMyTurn = false;
                else
                    startTurn();
                break;
            case "":
                System.out.println("Client received blank message");
                break;
            default:
                System.out.println("Message does not match cases");
                break;
        }
    }

    private void setWin(boolean youWon) {
        if (!gameOver) {
            if (youWon) {
                gameWinLabel.setText("You Won!");
            } else {
                gameWinLabel.setText("You Lost!");
            }

            gameOver = true;

            disableAllButtons();
            isMyTurn = false;
            turnLabel.setText("");
        }
    }

    private void disableAllButtons() {
        buildHouseButton.setDisable(true);
        buildRoadButton.setDisable(true);
        endTurnButton.setDisable(true);
        startGameButton.setDisable(true);
        rollDiceButton.setDisable(true);
    }


    private void incrementResource(String receivedMessage) {
        String[] message = receivedMessage.split(":");
        Card card = new Card();
        card.setResource(Resource.valueOf(message[1]));
        hand.addCard(card);
        updateHandText();
    }

    private void updateHandText() {
        int brick = 0;
        int wool = 0;
        int lumber = 0;
        int ore = 0;
        int grain = 0;
        for (Card card : hand.getHand()) {
            switch (Resource.valueOf(card.getResource().name())) {
                case BRICK:
                    brick++;
                    break;
                case WOOL:
                    wool++;
                    break;
                case LUMBER:
                    lumber++;
                    break;
                case ORE:
                    ore++;
                    break;
                case GRAIN:
                    grain++;
                    break;
            }
        }

        brickCountLabel.setText(String.valueOf(brick));
        woolCountLabel.setText(String.valueOf(wool));
        lumberCountLabel.setText(String.valueOf(lumber));
        oreCountLabel.setText(String.valueOf(ore));
        grainCountLabel.setText(String.valueOf(grain));
    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            if (line != null && !line.equals("")) {
                System.out.println("Client received message - " + line);
                handleMessage(line);
            }
        }

        @Override
        public void onClosedStatus(boolean isClosed) {
            if (isClosed) {
                notifyDisconnected();
                if (isAutoConnected) {

                } else {

                }
            } else {
                setIsConnected(true);
                if (isAutoConnected) {

                } else {

                }
            }
        }
    }
}
