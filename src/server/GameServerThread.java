package server;

import controllers.MainMenuController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.GameClientThread;
import models.ServerLog;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServerThread extends Thread {

    protected DatagramSocket datagramSocket;
    public GameServerThread() throws IOException {
        this("GameServerThread");
    }



    public GameServerThread(String name) throws IOException {
        super(name);
        datagramSocket = new DatagramSocket(4445);
    }

    public void run() {
        System.out.println("NEW SERVER THREAD RUNNING");
        openServerLog();

        String receivedMessage = "";
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while (!receivedMessage.startsWith("EXIT")) {

            receiveRequest(packet);

            byte[] receivedBytes = packet.getData();

            try {
                receivedMessage = new String(receivedBytes, "UTF-8");
                System.out.println("SERVER LOG: " + "received " + receivedMessage);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Figure out response
            String dString = "FROM SERVER THREAD";
            buf = dString.getBytes();

            sendResponse(packet, buf);
        }

        datagramSocket.close();
        System.out.println("Server closed socket");
    }

    private void receiveRequest(DatagramPacket packet) {
        // Receive request
        try {
            datagramSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendResponse(DatagramPacket packet, byte[] buf) {
        // Send the response to the client at "address" and "port"
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openServerLog() {


//        try {
//            new ServerLog().start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

}
