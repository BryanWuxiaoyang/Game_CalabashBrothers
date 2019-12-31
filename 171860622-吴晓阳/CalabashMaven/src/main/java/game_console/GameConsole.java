package game_console;

import basic.basics.Item;
import javafx.animation.Animation;
import javafx.scene.SubScene;
import maps.gamemap.GameMap;
import utils.workqueues.WorkQueue;
import task.Task;

import java.util.Collection;

public interface GameConsole {
    boolean add(Item item);

    boolean addWaitRunning(Item item);

    boolean add(Task task);

    boolean add(Animation animation);

    boolean remove(Item item);

    boolean remove(Task task);

    boolean remove(Animation animation);

    void start();

    void pause();

    void continueGame();

    void stop();

    GameState getGameState();

    SubScene getMainScene();

    GameMap getGameMap();

    SubScene getGameMapScene();

    WorkQueue getWorkQueue();

    SubScene getItemScene();

    Collection<Item> getItemSet();
}
