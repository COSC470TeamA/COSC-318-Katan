package models;

import controllers.GameController;
import controllers.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by steve on 2016-11-10.
 */
public class GameClientThread extends Application {
    Stage gameStage;
    DatagramSocket dsocket;
    GameController gameController;

    public GameClientThread() throws IOException {
        this("GameClientThread");
    }

    public GameClientThread(String name) throws IOException {
        super();
    }
    public GameClientThread(Stage stage) throws Exception {
        start(stage);
    }

    /**
     * Build a game stage and show it. Grabs a reference to the game stage's controller.
     * @param stageNOUSE
     * @throws Exception
     */
    @Override
    public void start(Stage stageNOUSE) throws Exception {

        try {
            System.out.println("Initializing Game Controller");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainMenuController.class.getResource("../views/Game.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gameController = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Katan");
            Scene gameScene = new Scene(anchorPane);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setScene(gameScene);

            stage.setResizable(true);
            stage.setAlwaysOnTop(false);

            // Constructor with no port number binds the DatagramSocket to any available port
        dsocket = new DatagramSocket();
        run();
        gameStage = stage;
        stage.setAlwaysOnTop(false);
        stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {

        // Add a change listener to the toServerLabel
        // Every time a change occurs in the label, the text is sent to the server
        gameController.getToServerLabel().textProperty().addListener((observable, oldValue, newValue) -> {
            sendRequest(newValue);
        });


        try {
            byte[] buf = new byte[256];

            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            sendRequest("FROM CLIENT");

            // get response
            packet = new DatagramPacket(buf, buf.length);
            receiveRequest(packet);


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prove that the Game Client Thread can communicate with the Game Controller
        //gameController.p("From GameClientThread");


    }

    private String receiveRequest(DatagramPacket packet) {
        String receivedMessage = "";
        try {
            dsocket.receive(packet);


        // display response
        byte[] receivedBytes = packet.getData();
        byte[] trimmed = new String(receivedBytes).trim().getBytes();


            receivedMessage = new String(trimmed, "UTF-8");
        receivedMessage.trim();
        System.out.println("CLIENT SEES: " + receivedMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return receivedMessage;
    }
    /**
     * Send a message to the server.
     *
     * @param message The message to be sent.
     */
    public void sendRequest(String message) {
        try {
            sendRequest(message.getBytes(), message.length(), InetAddress.getByName("localhost"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (message.startsWith("EXIT")) {
            System.out.println("Client closed socket");
            dsocket.close();
        }
    }

    public void sendRequest(byte[] buf, int length, InetAddress address) {

        try {
        DatagramPacket packet = new DatagramPacket(buf, length, address, 4445);
        packet.setLength(length);

            dsocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
