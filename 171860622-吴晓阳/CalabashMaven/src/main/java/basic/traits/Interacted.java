package basic.traits;

import basic.Effects.Effect;
import basic.basics.Item;
import javafx.geometry.Rectangle2D;


public interface Interacted extends Item {
    void acceptInteract(Effect effect);

    Rectangle2D getInteractedRange();
}
