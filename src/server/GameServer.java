package server;

import javafx.stage.Stage;
import models.ServerLog;

import java.io.IOException;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServer {
    public static void main(String[] args) throws Exception {
        new GameServerThread().start();

        new ServerLog().start(new Stage());
    }
}
