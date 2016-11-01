package models;

import javafx.scene.shape.Polygon;

/**
 * Created by haunter on 31/10/16.
 */
public class Hex {

    /** The height of a triangle with edges r and s at 30 degrees */
    private double h;
    /** The distance from the center of a hex to the edge */
    private double r;
    /** The x coordinate of the upper vertex */
    private double x;
    /** The y coordinate of the upper vertex */
    private double y;

    /** The length of one side of any hexagon */
    private double side = 60d;

    /**
     * Sets the points of a Polygon to be the coordinates of vertices of a hexagon.
     * @param hex The polygon to have its vertices mutated.
     * @param x The x coordinate of the upper vertex.
     * @param y The y coordinate of the upper vertex.
     */
    public void calculateVertices(Polygon hex, double x, double y) {
        h = calculateH(side);
        r = calculateR(side);

        hex.getPoints().setAll(
                x, y,
                x + side, y,
                x + side + h, y + r,
                x + side, y + r + r,
                x, y + r + r,
                x - h, y + r);

    }

    public void calculateVertices(Polygon hex, HexagonCoordinate coords) {

    }

    public static double calculateH(double side) {
        return (Math.sin(DegreesToRadians(30)) * side);
    }

    public static double calculateR(double side) {
        return (Math.cos(DegreesToRadians(30)) * side);
    }

    public static double DegreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }
}
