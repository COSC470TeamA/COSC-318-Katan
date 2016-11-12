package models;

import controllers.GameController;
import controllers.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

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

        try {
            byte[] buf = new byte[256];

            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            sendRequest(packet, buf, address);

            // get response
            packet = new DatagramPacket(buf, buf.length);
            dsocket.receive(packet);

            // display response
            String received = new String(packet.getData());
            System.out.println("CLIENT SEES: " + received);

            dsocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameController.p("From GameClientThread");
    }

    public void sendRequest(DatagramPacket packet, byte[] buf, InetAddress address) {

        buf = "Client thread is ready".getBytes();
        packet = new DatagramPacket(buf, buf.length, address, 4445);
        try {
            dsocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
