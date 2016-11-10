package models;

import server.GameServerThread;

import java.io.IOException;

/**
 * Created by steve on 2016-11-10.
 */
public class GameClient {
    public static void main(String[] args) throws IOException {
        new GameClientThread().start();
    }
}
