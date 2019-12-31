package game_console;

import basic.basics.Item;
import basic.traits.Emitter;
import javafx.animation.Animation;
import javafx.scene.SubScene;
import maps.gamemap.GameMap;
import task.Task;
import utils.workqueues.WorkQueue;

import java.util.Collection;

public class OverloadCheckGameConsole implements GameConsole{
    private final GameConsole console;

    private final int maxItemSize;

    private boolean overloadTag = false;

    public OverloadCheckGameConsole(GameConsole console, int maxItemSize){
        this.console = console;
        this.maxItemSize = maxItemSize;
    }

    private boolean tryAdd(){
        int size = getItemSet().size();
        return size <= maxItemSize;
        /*if(overloadTag) {
            if(size < maxItemSize * 3 / 4){
                overloadTag = false;
                return true;
            }
            else{
                return false;
            }
        }
        else {
            if(size >= maxItemSize){
                overloadTag = true;
                return false;
            }
            else return true;
        }*/
    }

    @Override
    public synchronized boolean add(Item item) {
        if(!tryAdd()) return false;
        boolean suc = console.add(item);
        if(suc) {
            //item.setOnRemove(event -> remove(item));
            //if (item instanceof Emitter) ((Emitter) item).setOnEmit(event -> addWaitRunning(event.getEmission()));
        }
        return suc;
    }

    @Override
    public synchronized boolean addWaitRunning(Item item) {
        if(!tryAdd()) return false;
        boolean suc = console.addWaitRunning(item);
        if(suc){
            //item.setOnRemove(event -> remove(item));
            //if(item instanceof Emitter) ((Emitter) item).setOnEmit(event -> addWaitRunning(event.getEmission()));
        }
        return suc;
    }

    @Override
    public boolean add(Task task) {
        return console.add(task);
    }

    @Override
    public boolean add(Animation animation) {
        return console.add(animation);
    }

    @Override
    public synchronized boolean remove(Item item) {
        return console.remove(item);
    }

    @Override
    public boolean remove(Task task) {
        return console.remove(task);
    }

    @Override
    public boolean remove(Animation animation) {
        return console.remove(animation);
    }

    @Override
    public synchronized void start() {
        console.start();
    }

    @Override
    public synchronized void pause() {
        console.pause();
    }

    @Override
    public synchronized void continueGame() {
        console.continueGame();
    }

    @Override
    public synchronized void stop() {
        console.stop();
    }

    @Override
    public GameState getGameState() {
        return console.getGameState();
    }

    @Override
    public SubScene getMainScene() {
        return console.getMainScene();
    }

    @Override
    public GameMap getGameMap() {
        return console.getGameMap();
    }

    @Override
    public WorkQueue getWorkQueue() {
        return console.getWorkQueue();
    }

    @Override
    public Collection<Item> getItemSet() {
        return console.getItemSet();
    }

    @Override
    public SubScene getGameMapScene() {
        return console.getGameMapScene();
    }

    @Override
    public SubScene getItemScene() {
        return console.getItemScene();
    }
}
