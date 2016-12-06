package server;

import client.Player;
import controllers.ServerLogController;
import javafx.application.Platform;
import javafx.collections.ObservableArray;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import models.*;
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
                case "dr":
                    setBroadcastMessage(receivedMessage + ":" + player.getColor());
                    break;
                case "rd":
                    setBroadcastMessage(receivedMessage + ":" + String.valueOf(dice.roll()));
                    break;
                case "dh":
                    //Add house to player who sent message
                    //Tell all players to draw house with specific color to player who sent packet
                    addHouseToClient(receivedMessage);
                    setBroadcastMessage(receivedMessage + ":" + player.getColor());
                    break;
                case "sg":
                    player.setMyTurn(true);
                    setBroadcastMessage(receivedMessage + ":" + player.getColor());
                case "et":
                    // Tell everyone that this player ended their turn
                    setBroadcastMessage(receivedMessage + ":" + player.getColor());
                case "":
                    dString = "Server received blank message";
                    break;
                default:
                    // Just send it back!
                    setBroadcastMessage(receivedMessage);
                    break;
            }
    }

    public void addHouseToClient(String receivedMessage) {
        try {
            String[] messageArray = receivedMessage.split(":");
            String[] tiles = messageArray[3].split("!");

            ArrayList<Tile> tilesList = new ArrayList<>();

            for (String tile : tiles) {
                if (!tile.equals("")) {
                    String[] tileInfo = tile.split("\\^");

                    String[] logicalCoord = tileInfo[0].split(",");
                    int x = Integer.parseInt(logicalCoord[0]);
                    int y = Integer.parseInt(logicalCoord[1]);

                    String[] info = tileInfo[1].split(",");
                    int rollMarker = Integer.parseInt(info[0]);
                    String resource = info[1];

                    HexagonCoordinate hexCoord = (new HexagonCoordinate(x, y));

                    tilesList.add(new Tile(hexCoord, Resource.valueOf(resource), new RollMarker(rollMarker)));
                }
            }

            player.getHouses().add(new House(tilesList));

        } catch (ArrayIndexOutOfBoundsException oob) {
            oob.printStackTrace();
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
