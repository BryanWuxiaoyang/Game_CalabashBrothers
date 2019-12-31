package basic.traits;

import basic.basics.Item;
import javafx.geometry.Rectangle2D;

public interface Interactor extends Item {
    void interactWith(Interacted item);

    Rectangle2D getInteractingRange();
}
