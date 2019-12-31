package basic.traits;

import basic.basics.Item;
import javafx.event.Event;
import javafx.event.EventType;

public class LifeChangeEvent extends Event {
    private double srcLife;

    private double dstLife;

    public LifeChangeEvent(Item source, double srcLife, double dstLife){
        super(source, null, EventType.ROOT);
        this.srcLife = srcLife;
        this.dstLife = dstLife;
    }

    public double getSrcLife() {
        return srcLife;
    }

    public double getDstLife() {
        return dstLife;
    }
}
