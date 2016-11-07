package models;

/**
 * Created by steve on 2016-11-07.
 */
public class Dice {
    Die redDie, blueDie;

    public int roll() {
        return redDie.roll() + blueDie.roll();
    }
}
