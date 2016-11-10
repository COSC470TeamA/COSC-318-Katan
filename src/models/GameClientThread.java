package models;

import controllers.MainMenuController;
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
public class GameClientThread extends Thread implements Initializable {

    protected DatagramSocket datagramSocket;

    public GameClientThread() throws IOException {
        this("GameClientThread");
    }
    public GameClientThread(String name) throws IOException {
        super(name);
    }

    public void run() {

        try {
            // get a datagram socket
            DatagramSocket dsocket = new DatagramSocket();

            // send request
            byte[] buf = new byte[256];
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            dsocket.send(packet);

            // get response
            packet = new DatagramPacket(buf, buf.length);
            dsocket.receive(packet);

            // display response
            String received = new String(packet.getData());
            System.out.println("CLIENT SEES: " + received);

            dsocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //showGameStage();

    }

    public void showGameStage() {
        System.out.println("Initializing Game Controller");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainMenuController.class.getResource("../views/Game.fxml"));
        AnchorPane anchorPane = null;
        try {
            anchorPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage gameStage = new Stage();
        gameStage.setTitle("Katan");
        Scene gameScene = new Scene(anchorPane);
        gameStage.initStyle(StageStyle.UTILITY);
        gameStage.setResizable(false);
        gameStage.setScene(gameScene);

        gameStage.setResizable(true);
        gameStage.setAlwaysOnTop(false);
        gameStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
