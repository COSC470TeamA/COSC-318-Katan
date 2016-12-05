package server;

import client.Player;
import controllers.ServerLogController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServer {
    private ServerSocket socket;

    //Map<Integer, Player> clients = new TreeMap<>();
    boolean victoryIsReached = false;
    int VICTORY_CONDITION = 4;

    private static final ArrayList<GameServerThread> clients = new ArrayList<>();


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
        for(GameServerThread client : clients) {
            client.sendMessage(message);
        }
    }
}

