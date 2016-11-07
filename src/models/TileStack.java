package models;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by steve on 2016-11-06.
 *
 * Represents a stack of game tiles. Each tile has resource.
 */
public class TileStack {
    /** The stack of tiles */
    ArrayList<Resource> stack = new ArrayList<>(19);

    /** Creates a stack of game tiles.
     *
     * A tile stack will be a random arrangement with
     * 4 * Lumber, 4 * Wool, 4 * Grain,
     * 3 * Brick, 3 * Ore, 1 * Desert.
     */
    public TileStack() {
        for (int i = 0; i < 4; i++) {
            stack.add(Resource.LUMBER);
            stack.add(Resource.WOOL);
            stack.add(Resource.GRAIN);
        }
        for (int i = 0; i < 3; i++) {
            stack.add(Resource.BRICK);
            stack.add(Resource.ORE);
        }
        stack.add(Resource.DESERT);

        Collections.shuffle(stack);
    }

    /**
     * Pops off a tile from the shuffled deck and returns it.
     *
     * @return The next tile in the shuffled stack.
     */
    public Resource next() {
        return stack.remove(0);
    }


}
