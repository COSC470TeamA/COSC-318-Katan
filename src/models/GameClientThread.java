package models;

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
public class GameClientThread extends Application implements Runnable {
    Stage gameStage;
    DatagramSocket dsocket;

    public GameClientThread() throws IOException {
        this("GameClientThread");
    }

    public GameClientThread(String name) throws IOException {
        super();
    }

    @Override
    public void start(Stage stage) throws Exception {
        dsocket = new DatagramSocket();
        run();
        gameStage = stage;
        stage.show();

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


    }

    public void sendRequest(DatagramPacket packet, byte[] buf, InetAddress address) {

        buf = "message from client".getBytes();
        packet = new DatagramPacket(buf, buf.length, address, 4445);
        try {
            dsocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
