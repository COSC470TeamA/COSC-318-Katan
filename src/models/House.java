package models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

/**
 * Created by Bre on 2016-12-04.
 */
public class House {
    private ArrayList<Tile> tiles;

    public House(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }
}
