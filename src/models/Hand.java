package models;

import java.util.ArrayList;

/**
 * Created by steve on 2016-11-11.
 */
public class Hand {

    ArrayList<Card> hand;

    public Hand() {
        hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public boolean removeCard(Card card) {
        return hand.remove(card);
    }

    public int getHandSize() {
        return hand.size();
    }

    public boolean contains(Card card) {
        return hand.contains(card);
    }

    public ArrayList<Card> getHand() {
        return hand;
    }
}
