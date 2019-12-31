package basic.traits;

import basic.basics.Item;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;


public interface Emitter extends Item {
    void setOnEmit(EventHandler<EmitEvent> handler);

    void setEmitEnabled(boolean tag);

    boolean isEmitEnabled();

    void emit();
}
