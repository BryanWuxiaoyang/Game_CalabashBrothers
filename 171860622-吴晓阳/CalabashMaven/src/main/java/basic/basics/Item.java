package basic.basics;

import basic.traits.RemoveEvent;
import game_console.GameConsole;
import javafx.animation.Animation;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.Collection;
import java.util.Comparator;

import javax.annotation.*;

public interface Item{
    ItemTypeId getItemTypeId();

    boolean isLoading();

    void setLoading(boolean tag);

    boolean isRemoved();

    void setRemoved(boolean tag);

    void setOnRemove(EventHandler<RemoveEvent> handler);

    void setConsole(GameConsole console);

    GameConsole getConsole();

    Node getNode();

    int getItemId();

    Duration getLivingTime();

    Collection<Animation> getAnimations();

    Comparator<Item> comparator = new Comparator<Item>() {
        public int compare(Item o1, Item o2) {
            return o1.getItemId() - o2.getItemId();
        }
    };

    Rectangle2D getSavedBounds();

    void setSavedBounds(Rectangle2D bounds);

    Rectangle2D getCurrentBounds();

    void saveCurrentBounds();
}
