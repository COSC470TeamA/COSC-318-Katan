package server;

import controllers.ServerLogController;
import javafx.application.Platform;
import javafx.collections.ObservableArray;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServerThread  extends Thread {

    protected DatagramSocket datagramSocket;
    String receivedMessage;
    ServerLogController serverLogController;
    Map<Integer, Integer> clients = new TreeMap<>();

    public GameServerThread() throws IOException {
        this("GameServerThread");
    }



    public GameServerThread(String name) throws IOException {
        super(name);
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




        String receivedMessage = "";
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while (true) {

            // Receive
            if (receiveRequest(packet).startsWith("EXIT")) {
                break;
            }
String dString;
            if (receivedMessage.equals("P")) {
                dString = "GOT P";
            }
else {
                // Figure out response
                dString = "FROM SERVER THREAD";
            }
            buf = dString.getBytes();

            // Send
            sendResponse(packet, buf);
        }

        datagramSocket.close();
        System.out.println("Server closed socket");
    }

    private String receiveRequest(DatagramPacket packet) {
        // Receive request
        try {
            datagramSocket.receive(packet);

            byte[] receivedBytes = packet.getData();
            byte[] trimmed = new String(receivedBytes).trim().getBytes();
            receivedMessage = new String(trimmed, "UTF-8");
            receivedMessage.trim();
            receivedMessage = receivedMessage.substring(0, packet.getLength());
            System.out.println("SERVER LOG: " + "received message: " + receivedMessage + packet.getPort());

            // Add the player #, port # mapping
            if (!clients.containsValue(packet.getPort())) {
                clients.put(clients.size() + 1, packet.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receivedMessage;
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
