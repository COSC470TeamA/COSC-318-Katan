package server;

import controllers.ServerLogController;
import javafx.application.Platform;
import javafx.collections.ObservableArray;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import models.Dice;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServerThread extends Thread {

    protected DatagramSocket datagramSocket;
    private int PORT_NUM = 4445;
    String receivedMessage;
    ServerLogController serverLogController;
    Map<Integer, Integer> clients = new TreeMap<>();
    boolean victoryIsReached = false;
    int VICTORY_CONDITION = 4;
    Dice dice = new Dice();

    public GameServerThread() throws IOException {
        this("GameServerThread");
    }


    /**
     * Spawns a game server thread and attaches a socket.
     * @param name
     * @throws IOException
     */
    public GameServerThread(String name) throws IOException {
        super(name);
        datagramSocket = new DatagramSocket(PORT_NUM);
    }

    /**
     * Listens for message coming in, determines a response and then
     * sends the response.
     *
     * Automatically invoked on construction.
     */
    public void run() {
        System.out.println("NEW SERVER THREAD RUNNING");



        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while (!victoryIsReached) {

            // Receive
            receiveRequest(packet);
            String dString;
            switch (receivedMessage) {
                case "P":
                    dString = "GOT P";
                    break;
                case "FROM CLIENT":
                    dString = "Server can see client";
                    break;
                case "rd":
                    dString = String.valueOf(dice.roll());
                    break;
                case "":
                    dString = "Server received blank message";
                    break;
                default:
                    // Figure out response
                    dString = "Message does not match cases";
                    break;
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
            System.out.println("SERVER LOG: " + "received message: " + receivedMessage + ": from port: " + packet.getPort());

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
