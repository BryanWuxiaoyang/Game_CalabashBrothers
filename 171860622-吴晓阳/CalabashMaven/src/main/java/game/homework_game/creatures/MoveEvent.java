package game.homework_game.creatures;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class MoveEvent extends Event {
    private int x;

    private int y;

    public MoveEvent(Object source, int x, int y) {
        super(source, null, null);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
