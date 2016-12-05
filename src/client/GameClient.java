package client;

import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by steve on 2016-11-10.
 */
public class GameClient {
    public static void main(String[] args) throws IOException {
        try {
            new GameClientThread().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
