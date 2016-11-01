package models;

/**
 * Created by haunter on 31/10/16.
 */
public class HexagonCoordinate {
    /** The x coordinate of a hexagon in a grid. */
    public int x;
    /** The y coordinate of a hexagon in a grid. */
    public int y;
    /** The z coordinate of a hexagon in a grid.
     * The horizontal row */
    public int z;

    public HexagonCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.z = - (x + y);
    }
}
