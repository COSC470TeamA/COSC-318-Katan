package models;

/**
 * Created by haunter on 31/10/16.
 */
public class HexagonCoordinate {
    /** The x coordinate of a hexagon in a grid. */
    public int x;
    /** The y coordinate of a hexagon in a grid. */
    public int y;


    public HexagonCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return  "x=" + x +
                ",y=" + y;
    }

}
