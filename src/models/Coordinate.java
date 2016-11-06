package models;

/**
 * Created by haunter on 31/10/16.
 */
public class Coordinate {
    /** The x coordinate of a hexagon in a grid. */
    public double x;
    /** The y coordinate of a hexagon in a grid. */
    public double y;

    /** Defines how close a mouse pointer needs to be to trigger selection of a vertex */
    public double CLOSE = 10;


    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isCloseTo(Coordinate b) {
        return Math.abs(this.getX() - b.getX()) < CLOSE && Math.abs(this.getY() - b.getY()) < CLOSE;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "HexagonCoordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}