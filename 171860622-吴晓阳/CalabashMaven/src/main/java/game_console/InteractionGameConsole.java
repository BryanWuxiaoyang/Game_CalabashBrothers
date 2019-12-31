package game_console;

import factory.TimeChecker;
import basic.basics.Item;
import basic.traits.Emitter;
import basic.traits.Interacted;
import basic.traits.Interactor;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.SubScene;
import javafx.util.Duration;
import maps.gamemap.GameMap;
import optimizations.InteractionGridMap;
import task.*;
import utils.MyConsumer;
import utils.workqueues.WorkQueue;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class InteractionGameConsole implements GameConsole {
    private GameConsole console;

    private boolean gridOptimizationEnable;

    private ReadWriteLock interactionLock = new ReentrantReadWriteLock();

    private Collection<Interactor> interactorSet = null;

    private Collection<Interacted> interactedSet = null;

    private final InteractionGridMap interactionGridMap;

    private void addToInteractorSet(Interactor item){
        if(gridOptimizationEnable && interactionGridMap != null) {
            item.saveCurrentBounds();
            item.setSavedBounds(item.getCurrentBounds());
            interactionGridMap.add(item);
        }
        else{
            interactorSet.add(item);
        }
    }

    private void addToInteractedSet(Interacted item){
        if(gridOptimizationEnable && interactionGridMap != null) {
            item.saveCurrentBounds();
            item.setSavedBounds(item.getCurrentBounds());
            interactionGridMap.add(item);
        }
        else{
            interactedSet.add(item);
        }
    }

    private void removeFromInteractorSet(Interactor item){
        if(gridOptimizationEnable && interactionGridMap != null){
            interactionGridMap.remove(item);
        }
        else{
            interactorSet.remove(item);
        }
    }

    private void removeFromInteractedSet(Interacted item){
        if(gridOptimizationEnable && interactionGridMap != null) {
            interactionGridMap.remove(item);
        }
        else{
            interactedSet.remove(item);
        }
    }

    private void interactionCheck(){
        interactionLock.readLock().lock();
        if (gridOptimizationEnable && interactionGridMap != null) {
            //AtomicInteger size = new AtomicInteger(0);
            interactionGridMap.forEach(new MyConsumer<InteractionGridMap.InteractionSet>() {
                public void accept(InteractionGridMap.InteractionSet set) {
                    for (final Interactor interactor : set.getInteractors()) {
                        final Rectangle2D interactorRange = interactor.getInteractingRange();
                        if (interactorRange != null) {
                            interactionGridMap.forEach((int) interactorRange.getMinX(), (int) interactorRange.getMinY(), (int) interactorRange.getWidth(), (int) interactorRange.getHeight(),
                                    new MyConsumer<InteractionGridMap.InteractionSet>() {
                                        public void accept(InteractionGridMap.InteractionSet set2) {
                                            for (Interacted interacted : set2.getInteracteds()) {
                                                Rectangle2D interactedBounds = interacted.getCurrentBounds();
                                                if (interactedBounds != null && interactorRange.intersects(interactedBounds)) {
                                                    interactor.interactWith(interacted);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                }
            });
            //if(getItemSet().size() % 5 == 0) System.out.println(getItemSet().size());
            //System.out.println("actual size: " + size);
            //System.out.println("itemSet size: " + getItemSet().size());
        }
        else {
            for(Interactor interactor: interactorSet){
                Rectangle2D interactorRange = interactor.getInteractingRange();
                if (interactorRange != null) {
                    for(Interacted interacted: interactedSet){
                        Rectangle2D bounds = interacted.getCurrentBounds();
                        if (bounds != null && interactorRange.intersects(bounds)) {
                            interactor.interactWith(interacted);
                        }
                    }
                }
            }

        }
        interactionLock.readLock().unlock();
    };

    public InteractionGameConsole(GameConsole console){
        this(console, false);
    }

    public InteractionGameConsole(GameConsole console, boolean gridOptimizationEnable){
        this.console = console;
        this.gridOptimizationEnable = gridOptimizationEnable;
        this.interactionGridMap = gridOptimizationEnable ? new InteractionGridMap((int) console.getGameMap().getWidth(), (int) console.getGameMap().getHeight(), 100, 100) : null;
        this.interactorSet = gridOptimizationEnable ? new HashSet<Interactor>() : null;
        this.interactedSet = gridOptimizationEnable ? new HashSet<Interacted>() : null;

        console.add(new TimelineTask(new Runnable() {
            public void run() {
                interactionCheck();
            }
        }, Duration.millis(100), -1));
        if(gridOptimizationEnable) {
            console.add(new TimelineTask(new Runnable() {
                public void run() {
                    interactionLock.writeLock().lock();
                    interactionGridMap.refresh();
                    interactionLock.writeLock().unlock();
                }
            }, Duration.millis(200), -1));
        }
    }

    private void internalAdd(Item item){
        //item.setOnRemove(event -> remove(item));
        //if(item instanceof Emitter) ((Emitter) item).setOnEmit(event-> addWaitRunning(event.getEmission()));
        if(item instanceof Interactor) addToInteractorSet((Interactor) item);
        if(item instanceof Interacted) addToInteractedSet((Interacted) item);
    }

    private void internalRemove(Item item){
        if(item instanceof Interactor) removeFromInteractorSet((Interactor) item);
        if(item instanceof Interacted) removeFromInteractedSet((Interacted) item);
    }


    public synchronized boolean add(final Item item) {
        boolean suc = console.add(item);
        if (suc) Platform.runLater(new Runnable() {
            public void run() {
                interactionLock.writeLock().lock();
                internalAdd(item);
                interactionLock.writeLock().unlock();
            }
        });
        return suc;
    }


    public synchronized boolean addWaitRunning(final Item item) {
        boolean suc = console.addWaitRunning(item);
        if (suc) Platform.runLater(new Runnable() {
            public void run() {
                interactionLock.writeLock().lock();
                internalAdd(item);
                interactionLock.writeLock().unlock();
            }
        });
        return suc;
    }


    public synchronized boolean add(Task task) {
        return console.add(task);
    }


    public synchronized boolean add(Animation animation) {
        return console.add(animation);
    }


    public synchronized boolean remove(final Item item) {
        boolean suc = console.remove(item);
        if(suc) Platform.runLater(new Runnable() {
            public void run() {
                interactionLock.writeLock().lock();
                internalRemove(item);
                interactionLock.writeLock().unlock();
            }
        });
        return suc;
    }


    public boolean remove(Task task) {
        return console.remove(task);
    }


    public boolean remove(Animation animation) {
        return console.remove(animation);
    }


    public synchronized void start() {
        console.start();
    }


    public synchronized void pause() {
        console.pause();
    }


    public synchronized void continueGame() {
        console.continueGame();
    }


    public synchronized void stop() {
        console.stop();
    }


    public GameState getGameState() {
        return console.getGameState();
    }


    public SubScene getMainScene() {
        return console.getMainScene();
    }


    public GameMap getGameMap() {
        return console.getGameMap();
    }


    public WorkQueue getWorkQueue() {
        return console.getWorkQueue();
    }


    public Collection<Item> getItemSet() {
        return console.getItemSet();
    }


    public SubScene getGameMapScene() {
        return console.getGameMapScene();
    }


    public SubScene getItemScene() {
        return console.getItemScene();
    }
}
