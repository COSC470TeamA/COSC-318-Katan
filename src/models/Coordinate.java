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

    /**
     * Determines if a mouse coordinate is close to a vertex.
     * Close means that the coordinates are less than CLOSE pixels away.
     *
     * @param b The coordinate to be checked for closeness, against this.
     * @return
     */
    public boolean isCloseTo(Coordinate b) {
        return Math.abs(this.getX() - b.getX()) < CLOSE && Math.abs(this.getY() - b.getY()) < CLOSE;
    }

    public Coordinate midpoint(Coordinate b) {
        double midx = (this.getX() + b.getX()) / 2;
        double midy = (this.getY() + b.getY()) / 2;
        return new Coordinate(midx, midy);
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