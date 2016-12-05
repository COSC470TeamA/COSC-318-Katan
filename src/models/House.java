package models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

/**
 * Created by Bre on 2016-12-04.
 */
public class House {
    private ArrayList<HexagonCoordinate> tiles;

    public House(ArrayList<HexagonCoordinate> tiles) {
        this.tiles = tiles;
    }

    public ArrayList<HexagonCoordinate> getTiles() {
        return tiles;
    }
}
