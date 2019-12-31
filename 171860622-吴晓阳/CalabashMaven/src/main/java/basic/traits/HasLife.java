package basic.traits;

import basic.basics.Item;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface HasLife extends Item {
    double getMaxLife();

    void setMaxLife(double maxLife, boolean fillLife);

    double getLife();

    void increaseLife(double value);

    void decreaseLife(double value);

    void setOnDeath(EventHandler<ActionEvent> handler);

    void setOnLifeChanged(EventHandler<LifeChangeEvent> handler);
}