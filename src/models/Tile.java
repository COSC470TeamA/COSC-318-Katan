package models;

import javafx.scene.shape.Polygon;

/**
 * Created by steve on 2016-11-07.
 */
public class Tile {

    /** The logical position of the tile, row and column.
     * The physical position can be determined via Hex.
     */
    HexagonCoordinate logicalCoordinate;

    Polygon hex;

    /** The resource that the tile belongs to. */
    Resource resource;

    /** The roll marker on a tile which determines the probability. */
    RollMarker rollMarker;

    public Tile(HexagonCoordinate logicalCoordinate, Polygon hex, Resource resource, int rollMarker) {
        this.logicalCoordinate = logicalCoordinate;
        this.hex = hex;
        this.resource = resource;
        this.rollMarker = new RollMarker(rollMarker);
    }
    public Tile(HexagonCoordinate logicalCoordinate, Polygon hex, Resource resource, RollMarker rollMarker) {
        this.logicalCoordinate = logicalCoordinate;
        this.hex = hex;
        this.resource = resource;
        this.rollMarker = rollMarker;
    }

    public HexagonCoordinate getLogicalCoordinate() {
        return logicalCoordinate;
    }

    public void setLogicalCoordinate(HexagonCoordinate logicalCoordinate) {
        this.logicalCoordinate = logicalCoordinate;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public RollMarker getRollMarker() {
        return rollMarker;
    }

    public void setRollMarker(RollMarker rollMarker) {
        this.rollMarker = rollMarker;
    }

    public Polygon getHex() {
        return hex;
    }

    public void setHex(Polygon hex) {
        this.hex = hex;
    }
}
