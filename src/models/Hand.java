package models;

import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;

/**
 * Created by steve on 2016-11-11.
 */
public class Hand {

    ArrayList<Card> hand;


    public Hand() {
        hand = new ArrayList<>();
    }

    public boolean canAffordHouse() {
        return  this.contains(Resource.BRICK) && this.contains(Resource.LUMBER) &&
                this.contains(Resource.GRAIN) && this.contains(Resource.WOOL);
    }

    public boolean canAffordRoad() {
        return  this.contains(Resource.BRICK) && this.contains(Resource.LUMBER);
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public boolean removeCard(Card card) {
        return hand.remove(card);
    }

    private boolean removeCard(Resource resource) {
        for (Card c : hand) {
            if (c.getResource().equals(resource)) {
                hand.remove(c);
                return true;
            }
        }
        return false;
    }

    public void removeHouseCards() {
        removeCard(Resource.BRICK);
        removeCard(Resource.LUMBER);
        removeCard(Resource.WOOL);
        removeCard(Resource.GRAIN);
    }

    public void removeRoadCards() {
        removeCard(Resource.BRICK);
        removeCard(Resource.LUMBER);
    }

    public int getHandSize() {
        return hand.size();
    }

    private boolean contains(Card card) {
        return hand.contains(card);
    }

    public boolean contains(Resource resource) {
        for (Card c : hand) {
            if (c.getResource().equals(resource)) return true;
        }
        return false;
    }


    public ArrayList<Card> getHand() {
        return hand;
    }
}
