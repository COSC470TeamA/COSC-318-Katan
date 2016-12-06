package server;

import client.Player;
import controllers.ServerLogController;
import models.Card;
import models.House;
import models.PlayerColour;
import models.Tile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServer {
    private static boolean gameStarted = false;
    private ServerSocket socket;
    boolean victoryIsReached = false;
    int VICTORY_CONDITION = 4;

    private static final List<GameServerThread> clients = Collections.synchronizedList(new ArrayList<GameServerThread>());

    private static ArrayList<String> initialMessages = new ArrayList<>();


    public static void main(String[] args) throws Exception {
        int PORT_NUM = 4445;
        final ServerSocket serverSocket  = new ServerSocket(PORT_NUM);
        Socket socket = null;


        Thread accept = new Thread() {
            public void run(){
                while(true){
                    try{
                        Socket s = serverSocket.accept();
                        GameServerThread client = new GameServerThread(s, new Player());
                        client.start();
                        clients.add(client);
                    }
                    catch(IOException e){ e.printStackTrace(); }
                }
            }
        };

        accept.setDaemon(true);
        accept.start();

        Thread listen = new Thread(){
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
                while(true){
                    String message = checkClientMessages();
                    if (message != null) {
                        sendToAllClients(message);
                    }
                }
            }
        };

        listen.setDaemon(true);
        listen.start();
    }

    private static String checkClientMessages() {
        for(GameServerThread client : clients) {
            String message = client.getBroadcastMessage();
            if (message != null) {
                client.setBroadcastMessage(null);
                return message;
            }
        }
        return null;
    }



    private static void sendToAllClients(String message){
        if (message.startsWith("sg") && clients.size() == 2 && !gameStarted) {
            gameStarted = true;
            initializeGame();
            return;
        } else if (message.startsWith("rd")) {
            dollOutResources(message);
        }

        for(GameServerThread client : clients) {
            if (message.startsWith("sg")) {
                client.sendMessage(message + ":" + client.player.getColor());
            } else  {
                client.sendMessage(message);
            }
        }
    }

    private static void dollOutResources(String message) {
        int roll = Integer.valueOf(message.split(":")[1]);

        for(GameServerThread client: clients) {
            for (House house : client.player.getHouses()) {
                for (Tile tile : house.getTiles()) {
                    if (tile.getRollMarker().getRoll() == roll) {
                        //player this resource to player hand
                        Card card = new Card();
                        card.setResource(tile.getResource());
                        client.player.getHand().addCard(card);

                        //tell client to increment resources in gui
                        client.sendMessage("ir:" + card.getResource().name());
                    }
                }
            }
        }
    }

    private static void initializeGame() {
        ArrayList<ArrayList<String>> initialMessages = getInitialMessages();

        for(int i = 0; i < 2; i++) {
            ArrayList<String> messages = initialMessages.get(i);

            for(String string : messages) {
                string = string + clients.get(i).player.getColor();

                if (string.startsWith("dh")) {
                    clients.get(i).addHouseToClient(string);
                }

                for(GameServerThread client : clients) {
                    client.sendMessage(string);
                }
            }
        }
    }

    private static ArrayList<ArrayList<String>> getInitialMessages() {
        ArrayList<String> client1Messages = new ArrayList<>();
        client1Messages.add("dr:256.20508075688775:125.0:0.0:");
        client1Messages.add("dh:256.20508075688775:100.0:0,3!1,2!1,3!:");
        client1Messages.add("dr:300.20508075688775:208.0:0.0:");
        client1Messages.add("dh:298.20508075688775:171.0:2,4!1,3!2,3!:");

        ArrayList<String> client2Messages = new ArrayList<>();
        client2Messages.add("dr:111.49226701332081:242.88972603279078:60.0:");
        client2Messages.add("dh:88.20508075688775:248.0:3,1!3,0!2,1!:");
        client2Messages.add("dr:194.4800670631703:308.8493623411972:120.0:");
        client2Messages.add("dh:210.20508075688775:322.0:4,2!3,2!4,3!:");

        ArrayList<ArrayList<String>> startupMessages = new ArrayList<>();
        startupMessages.add(client1Messages);
        startupMessages.add(client2Messages);

        return startupMessages;
    }
}

