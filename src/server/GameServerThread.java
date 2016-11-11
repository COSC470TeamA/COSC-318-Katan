package server;

import controllers.ServerLogController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServerThread  extends Thread {

    protected DatagramSocket datagramSocket;
    String receivedMessage;
    ServerLogController serverLogController;

    public GameServerThread() throws IOException {
        this("GameServerThread");
    }



    public GameServerThread(String name) throws IOException {
        //super(name);
        datagramSocket = new DatagramSocket(4445);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/ServerLog.fxml"));
        try {
            Parent root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverLogController = loader.getController();
    }

    public void run() {
        System.out.println("NEW SERVER THREAD RUNNING");

//        showServerLog();

        String receivedMessage = "";
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while (!receivedMessage.startsWith("EXIT")) {
            receiveRequest(packet);


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
            byte[] receivedBytes = packet.getData();
            byte[] trimmed = new String(receivedBytes).trim().getBytes();
            receivedMessage = new String(trimmed, "UTF-8");
            receivedMessage.trim();
            System.out.println("SERVER LOG: " + "received " + receivedMessage);


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




}
