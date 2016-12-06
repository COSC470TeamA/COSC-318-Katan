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
    boolean victoryIsReached = false;
    int VICTORY_CONDITION = 4;

    private static final List<GameServerThread> clients = Collections.synchronizedList(new ArrayList<GameServerThread>());

    public static void main(String[] args) throws Exception {
        int PORT_NUM = 4445;
        final ServerSocket serverSocket  = new ServerSocket(PORT_NUM);

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
        if (message.startsWith("rd")) {
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
}

