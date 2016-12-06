package models;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by steve on 2016-12-05.
 */
public class PlayerColour {
    static ArrayList<Color> colours;
    public PlayerColour() {
        colours = new ArrayList<>(8);
        colours.add(Color.LIMEGREEN);
        colours.add(Color.CYAN);
        colours.add(Color.FUCHSIA);
        colours.add(Color.CHOCOLATE);
        Collections.shuffle(colours);
    }
    public static Color getPlayColour() {
if (colours == null) new PlayerColour();
        return colours.remove(0);
    }
}
