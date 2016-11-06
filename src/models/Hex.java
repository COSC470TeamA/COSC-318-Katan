package models;

import javafx.scene.shape.Polygon;

import static java.lang.Thread.sleep;

/**
 * Created by haunter on 31/10/16.
 */
public class Hex {

    /** The length of one side of any hexagon */
    public static double side = 35d;

    /** The height of a triangle with edges r and s at 30 degrees */
    private double h = calculateH(side);
    /** The distance from the center of a hex to the edge */
    private double r = calculateR(side);
    /** The x coordinate of the upper vertex */
    private double x;
    /** The y coordinate of the upper vertex */
    private double y;
    /** Y distance from the upper left vertex of a hex to one below it */
    private double DISTANCE_VERTICAL = h + side;
    /** X distance from the upper left vertex of a hex to one to the right */
    private double DISTANCE_HORIZONTAL = 2 * r;


    /**
     * Sets the points of a Polygon to be the coordinates of vertices of a hexagon.
     * @param hex The polygon to have its vertices mutated.
     * @param x The x coordinate of the upper vertex.
     * @param y The y coordinate of the upper vertex.
     */
    public void calculateVertices(Polygon hex, double x, double y) {
        //x += DISTANCE_HORIZONTAL; // Move the whole grid over

        hex.getPoints().setAll(
                x, y,
                x + r, y + h,
                x + r, y + side + h,
                x, y + side + h + h,
                x - r, y + side + h,
                x - r, y + h);
    }

    public void calculateVertices(Polygon hex, HexagonCoordinate coords) {

        int row = coords.getX();
        int col = coords.getY();
        // If the row is odd get the proper offset
        double oddRowOffset = (row + 1) % 2 == 0 ? r : 0;

        calculateVertices(hex, DISTANCE_HORIZONTAL * col + oddRowOffset, DISTANCE_VERTICAL * row);


    }
    public static double getSideLength() { return side; }

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
