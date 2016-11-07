package models;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by steve on 2016-11-07.
 */
public class RollMarkerStack {
    /** The stack of markers */
    ArrayList<RollMarker> stack = new ArrayList<>(18);

    /** Creates a stack of roll markers.
     *
     * A tile stack will be a random arrangement.
     */
    public RollMarkerStack() {
        for (int i = 0; i < 2; i++) {
            stack.add(new RollMarker(3));
            stack.add(new RollMarker(4));
            stack.add(new RollMarker(5));
            stack.add(new RollMarker(6));
            stack.add(new RollMarker(8));
            stack.add(new RollMarker(9));
            stack.add(new RollMarker(10));
            stack.add(new RollMarker(11));
        }
        stack.add(new RollMarker(2));
        stack.add(new RollMarker(12));

        Collections.shuffle(stack);
    }

    /**
     * Pops off a roll marker from the shuffled deck and returns it.
     *
     * @return The next marker in the shuffled stack.
     */
    public RollMarker next() {
        return stack.remove(0);
    }
}
