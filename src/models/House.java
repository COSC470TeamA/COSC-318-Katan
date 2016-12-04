package models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

/**
 * Created by Bre on 2016-12-04.
 */
public class House {
    private Color color;
    private Polygon polygon;
    private ArrayList<Tile> tiles;

    public House(Color color, Polygon polygon, ArrayList<Tile> tiles) {
        this.color = color;
        this.polygon = polygon;
        this.tiles = tiles;
    }

    public Color getColor() {
        return color;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }
}
