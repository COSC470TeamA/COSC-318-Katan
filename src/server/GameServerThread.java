package server;

import client.Player;
import controllers.ServerLogController;
import javafx.application.Platform;
import javafx.collections.ObservableArray;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import models.Dice;
import socketfx.Constants;
import socketfx.FxSocketServer;
import socketfx.SocketListener;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServerThread extends Thread {
    Socket client;
    Dice dice;
    Player player;

    private String broadcastMessage;
    private BufferedWriter output = null;
    private BufferedReader input = null;

    public GameServerThread(Socket client, Player player) throws IOException {
        this("GameServerThread");
        this.client = client;
        this.player = player;

        dice = new Dice();

        if (client!= null && !client.isClosed()) {
            input = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream()));
        }

        Thread read = new Thread(){
            public void run(){
                while(true){
                    try{
                        String message = input.readLine();
                        System.out.println("Server received message - " + message);
                        handleMessage(message);
                    }
                    catch(IOException e){ e.printStackTrace(); }
                }
            }
        };

        read.setDaemon(true);
        read.start();
    }

    /**
     * Spawns a game server thread and attaches a socket.
     * @param name
     * @throws IOException
     */
    public GameServerThread(String name) throws IOException {
        super(name);
    }

    public void handleMessage(String receivedMessage) {
            String dString;

            String[] receivedMessageArray = receivedMessage.split(":");
            switch (receivedMessageArray[0]) {
                case "P":
                    dString = "GOT P";
                    break;
                case "dr":
                    setBroadcastMessage(receivedMessage + ":" + player.getColor());
                    break;
                case "rd":
                    setBroadcastMessage(receivedMessage + ":" + String.valueOf(dice.roll()));
                    break;
                case "dh":
                    //Add house to player who sent message
                    //Tell all players to draw house with specific color to player who sent packet
                    setBroadcastMessage(receivedMessage + ":" + player.getColor());
                    break;
                case "":
                    dString = "Server received blank message";
                    break;
                default:
                    // Figure out response
                    setBroadcastMessage(receivedMessage);
                    break;
            }
    }

    public void sendMessage(String msg) {
        try {
            output.write(msg, 0, msg.length());
            output.newLine();
            output.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public void setBroadcastMessage(String broadcastMessage) {
        this.broadcastMessage = broadcastMessage;
    }
}
