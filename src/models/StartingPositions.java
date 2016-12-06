package models;

import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by steve on 2016-12-05.
 */
public class StartingPositions {
    ArrayList<Coordinate> mouseEvents = new ArrayList<>(8);

    public static StartingPositions instance = null;

    private StartingPositions() {

//        mouseEvents.add("dr:256.20508075688775:125.0:0.0:");
//        mouseEvents.add("dh:256.20508075688775:100.0:0,3!1,2!1,3!:");
//        mouseEvents.add("dr:300.20508075688775:208.0:0.0:");
//        mouseEvents.add("dh:298.20508075688775:171.0:2,4!1,3!2,3!:");
//
//        mouseEvents.add("dr:111.49226701332081:242.88972603279078:60.0:");
//        mouseEvents.add("dh:88.20508075688775:248.0:3,1!3,0!2,1!:");
//        mouseEvents.add("dr:194.4800670631703:308.8493623411972:120.0:");
//        mouseEvents.add("dh:210.20508075688775:322.0:4,2!3,2!4,3!:");
        Coordinate c = new Coordinate(130, 75);//
        mouseEvents.add(c);
        c = new Coordinate(87, 125);
        mouseEvents.add(c);
        c = new Coordinate(261, 301);
        mouseEvents.add(c);
        c = new Coordinate(283, 234);//
        mouseEvents.add(c);

        c = new Coordinate(111, 242);
        mouseEvents.add(c);
        c = new Coordinate(88, 248);
        mouseEvents.add(c);
        c = new Coordinate(194, 308);
        mouseEvents.add(c);
        c = new Coordinate(210, 322);
        mouseEvents.add(c);

        //Collections.shuffle(mouseEvents);
    }
    public static void robotClick(int x, int y) {
        Robot bot = null;
        try {
            bot = new Robot();
        bot.mouseMove(x, y);
        bot.mousePress(InputEvent.BUTTON1_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void robotClick(double x, double y) {
        robotClick((int) x, (int) y);
    }

    public static StartingPositions getInstance() {
        if (instance == null) {
            instance = new StartingPositions();
        }
        return instance;
    }

    public Coordinate getMouseEvent() {
        return mouseEvents.remove(0);
    }

}
