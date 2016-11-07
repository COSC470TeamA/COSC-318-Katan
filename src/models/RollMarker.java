package models;

/**
 * Created by steve on 2016-11-07.
 */
public class RollMarker {
    int roll;
    public RollMarker(int roll) {
        if (roll < 1 || roll > 12 || roll == 7) {
            System.err.println("Roll marker of " + roll + " cannot be created!");
        }
        else
            this.roll = roll;
    }

    public int getRoll() {
        return roll;
    }

}
