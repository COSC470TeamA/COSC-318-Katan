package client;

import javafx.scene.paint.Color;
import models.Hand;
import models.House;
import models.PlayerColour;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Bre on 2016-12-04.
 */
public class Player {
    private Hand hand;
    private ArrayList<House> houses;
    private Color color;
    private boolean isMyTurn;

    public Player() {
        //this.color = getRandomColor();
        this.color = PlayerColour.getPlayColour();
        this.houses = new ArrayList<>();
        this.hand = new Hand();
    }

    private Color getRandomColor() {
        Random rand = new Random();

        // generate the random integers for r, g and b value
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        return Color.rgb(r,g,b);
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<House> getHouses() {
        return houses;
    }

    public Hand getHand() {
        return hand;
    }

}

