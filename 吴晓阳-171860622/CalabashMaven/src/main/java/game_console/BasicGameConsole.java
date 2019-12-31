package game_console;

import basic.basics.Item;
import basic.traits.*;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.SubScene;
import maps.gamemap.GameMapImp1;
import task.AssignmentPoolTask;
import task.Task;
import javafx.animation.Animation;
import javafx.geometry.Bounds;
import javafx.util.Duration;
import maps.gamemap.GameMap;
import task.TimelineTask;
import task.WorkQueueTask;
import utils.Utils;
import utils.workqueues.BasicWorkQueue;
import utils.workqueues.FXWorkQueue;
import utils.workqueues.WorkQueue;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class BasicGameConsole extends SubScene implements GameConsole {
    private WorkQueue workQueue = new BasicWorkQueue();

    private Collection<Item> itemSet = new ConcurrentLinkedQueue<Item>();

    private Collection<Task> externalTasks = new ConcurrentLinkedQueue<Task>();

    private Collection<Animation> externalAnimations = new ConcurrentLinkedQueue<Animation>();

    private Collection<Thread> waitThreads = new CopyOnWriteArrayList<Thread>();

    private GameState gameState = GameState.STOP;

    private SubScene gameMapScene;

    private SubScene itemGroupScene;

    private GameMap gameMap;

    private Group root = new Group();

    private Group itemGroup = new Group();

    private Task rangeCheck = new TimelineTask(new Runnable() {
        public void run() {
            for (Item item : itemSet) {
                Bounds bounds = item.getNode().getBoundsInParent();
                if (bounds.getMinX() < 0 || bounds.getMinX() > gameMap.getWidth()
                        || bounds.getMinY() < 0 || bounds.getMinY() > gameMap.getHeight()) {
                    item.setRemoved(true);
                }
            }
        }
    }, Duration.millis(2000), -1);

    private Task externalTaskCheck = new AssignmentPoolTask(new Runnable() {
        public void run() {
            Iterator<Task> iterator = externalTasks.iterator();
            Task task;
            while (iterator.hasNext()) {
                task = iterator.next();
                if (task.isDone()) iterator.remove();
            }
        }
    }, Duration.millis(2000), -1);

    private Task externalAnimationCheck = new AssignmentPoolTask(new Runnable() {
        public void run() {
            Iterator<Animation> iterator = externalAnimations.iterator();
            Animation animation;
            while (iterator.hasNext()) {
                animation = iterator.next();
                if (animation.getStatus() == Animation.Status.STOPPED) iterator.remove();
            }
        }
    }, Duration.millis(2000), -1);

    private Task boundsRefreshTask = new WorkQueueTask(new Runnable() {
        public void run() {
            for(Item item: itemSet){
                item.saveCurrentBounds();
            }
        }
    }, Duration.millis(30), -1, FXWorkQueue.getInstance());

    private Collection<Task> FXRelatedTasks = new ArrayList<Task>(Arrays.asList(
            rangeCheck
            //boundsRefreshTask
    ));

    private Collection<Task> runningPhaseTasks = new ArrayList<Task>(Arrays.asList(
            externalTaskCheck,
            externalAnimationCheck
    ));

    public BasicGameConsole(){
        this(null);
    }

    public BasicGameConsole(GameMap gameMap) {
        super(new Group(), gameMap != null ? gameMap.getWidth() : 1000, gameMap != null ? gameMap.getHeight() : 1000);
        this.gameMap = gameMap;
        this.setRoot(root);

        if(gameMap != null) {
            gameMapScene = new SubScene(gameMap.getNode(), gameMap.getWidth() * 2, gameMap.getHeight() * 2);
            itemGroupScene = new SubScene(itemGroup, gameMap.getWidth(), gameMap.getHeight());
            root.getChildren().addAll(gameMapScene, itemGroupScene);
        }
        else{
            gameMapScene = null;
            itemGroupScene = new SubScene(itemGroup, 1000, 1000);
            root.getChildren().addAll(itemGroupScene);
        }
    }

    public void start() {
        if (gameState == GameState.PAUSE || gameState == GameState.STOP) {
            {

                for(Task task: FXRelatedTasks){
                    task.run();
                }
                for(Task task: runningPhaseTasks){
                    task.run();
                }
                for(Task task: externalTasks){
                    task.run();
                }
                for(Animation animation: externalAnimations){
                    animation.play();
                }
                for(Item item: itemSet){
                    if(item.getAnimations() != null){
                        for(Animation animation: item.getAnimations()){
                            animation.play();
                        }
                    }
                }

                awakeThreads();
                gameState = GameState.RUNNING;
            }
        }
    }

    public void pause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSE;

            for(Task task: FXRelatedTasks){
                task.pause();
            }
            for(Task task: runningPhaseTasks){
                task.pause();
            }
            for(Task task: externalTasks){
                task.pause();
            }
            for(Animation animation: externalAnimations){
                animation.pause();
            }
            for(Item item: itemSet){
                if(item.getAnimations() != null){
                    for(Animation animation: item.getAnimations()){
                        animation.pause();
                    }
                }
            }
        }
    }

    public void continueGame() {
        if (gameState == GameState.PAUSE) {
            for(Task task: FXRelatedTasks){
                task.run();
            }
            for(Task task: runningPhaseTasks){
                task.run();
            }
            for(Task task: externalTasks){
                task.run();
            }
            for(Animation animation: externalAnimations){
                animation.play();
            }
            for(Item item: itemSet){
                if(item.getAnimations() != null){
                    for(Animation animation: item.getAnimations()){
                        animation.play();
                    }
                }
            }

            gameState = GameState.RUNNING;
            awakeThreads();
        }
    }

    public void stop() {
        if (gameState == GameState.RUNNING || gameState == GameState.PAUSE) {
            gameState = GameState.STOP;

            for(Task task: FXRelatedTasks){
                task.stop();
            }
            for(Task task: runningPhaseTasks){
                task.stop();
            }
            for(Task task: externalTasks){
                task.stop();
            }
            for(Animation animation: externalAnimations){
                animation.stop();
            }
            for(Item item: itemSet){
                if(item.getAnimations() != null){
                    for(Animation animation: item.getAnimations()){
                        animation.stop();
                    }
                }
            }

            Collection<Item> removeSet = new ArrayList<Item>(itemSet);
            for(Item item: removeSet){
                item.setRemoved(true);
            }
        }
    }

    private void addToPane(Item item){
        itemGroup.getChildren().add(item.getNode());
    }

    private boolean addToSet(Item item){
        //item.setOnRemove(event -> remove(item));
        /*if(item instanceof Emitter) ((Emitter) item).setOnEmit(event -> {
            addWaitRunning(event.getEmission());
        });*/
        if(gameState == GameState.RUNNING && item.getAnimations() != null) {
            for(Animation animation: item.getAnimations()){
                if(animation.getStatus() != Animation.Status.RUNNING){
                    animation.play();
                }
            }
        }
        return itemSet.add(item);
    }

    private void removeFromPane(Item item){
        itemGroup.getChildren().remove(item.getNode());
    }

    private boolean removeFromSet(Item item){
        //if(item instanceof Emitter) ((Emitter) item).setEmitEnabled(false);
        boolean suc = itemSet.remove(item);
        if(item.getAnimations() != null){
            for(Animation animation: item.getAnimations()){
                if (animation.getStatus() == Animation.Status.RUNNING) {
                    animation.stop();
                }
            }
        }
        return suc;
    }

    private void awakeThreads(){
        for(Thread thread: waitThreads){
            thread.interrupt();
        }
        waitThreads.clear();
    }

    public boolean addWaitRunning(Item item){
        while(gameState != GameState.RUNNING){
            waitThreads.add(Thread.currentThread());
            Utils.sleep(Integer.MAX_VALUE);
        }

        return add(item);
    }

    public boolean add(final Item item){
        Platform.runLater(new Runnable() {
            public void run() {
                boolean suc = addToSet(item);
                if(suc) addToPane(item);
            }
        });
        return true;
    }

    public boolean add(final Task task){
        Platform.runLater(new Runnable() {
            public void run() {
                boolean suc = externalTasks.add(task);
                if(suc && gameState == GameState.RUNNING) task.run();
            }
        });

        return true;
    }

    public boolean add(final Animation animation){
        Platform.runLater(new Runnable() {
            public void run() {
                boolean suc = externalAnimations.add(animation);
                if(suc && gameState == GameState.RUNNING) animation.play();
            }
        });

        return true;
    }

    public boolean remove(final Item item){
        Platform.runLater(new Runnable() {
            public void run() {
                removeFromSet(item);
                removeFromPane(item);
            }
        });
        return true;
    }

    public boolean remove(final Task task){
        Platform.runLater(new Runnable() {
            public void run() {
                externalTasks.remove(task);
            }
        });
        return true;
    }

    public boolean remove(final Animation animation){
        Platform.runLater(new Runnable() {
            public void run() {
                externalAnimations.remove(animation);
            }
        });
        return true;
    }

    public GameMap getGameMap(){
        return gameMap;
    }

    public SubScene getGameMapScene(){
        return gameMapScene;
    }

    public GameState getGameState(){
        return gameState;
    }


    public SubScene getMainScene() {
        return this;
    }


    public WorkQueue getWorkQueue() {
        return workQueue;
    }


    public SubScene getItemScene(){
        return itemGroupScene;
    }


    public Collection<Item> getItemSet() {
        return itemSet;
    }
}
