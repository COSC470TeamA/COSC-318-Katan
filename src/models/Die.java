package models;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by steve on 2016-11-07.
 */
public class Die {

    public int roll() {
        return ThreadLocalRandom.current().nextInt(1, 7);
    }
}
