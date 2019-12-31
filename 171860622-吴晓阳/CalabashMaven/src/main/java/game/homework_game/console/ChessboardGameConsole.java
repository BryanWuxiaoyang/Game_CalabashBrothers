package game.homework_game.console;

import basic.basics.Item;
import game.homework_game.creatures.ChessboardCreature;
import game_console.GameConsole;
import game_console.GameState;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SubScene;
import maps.gamemap.ChessboardGameMap;
import maps.grids.Grid;
import task.Task;
import utils.Utils;
import utils.workqueues.WorkQueue;

import java.util.*;
import java.util.concurrent.locks.*;

public class ChessboardGameConsole implements GameConsole {
    private final GameConsole console;

    private final ChessboardGameMap gameMap;

    private final ChessboardGrid[][] chessboard;

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    public ChessboardGameConsole(GameConsole console) {
        this.console = console;
        this.gameMap = (ChessboardGameMap) console.getGameMap();
        this.chessboard = new ChessboardGrid[gameMap.getColSize()][gameMap.getRowSize()];
        for(int i = 0; i < gameMap.getColSize(); i++){
            for(int j = 0; j < gameMap.getRowSize(); j++){
                chessboard[i][j] = new ChessboardGrid();
            }
        }
    }

    public synchronized boolean addToChessboard(ChessboardCreature creature, int dstX, int dstY){
        boolean suc;
        ChessboardGrid grid = chessboard[dstX][dstY];
        grid.getCreatures().add(creature);
        creature.setX(dstX);
        creature.setY(dstY);

        suc = true;
        return suc;
    }

    public synchronized void removeFromChessboard(ChessboardCreature creature){
        int x = creature.getX();
        int y = creature.getY();

        chessboard[x][y].getCreatures().remove(creature);
    }

    public synchronized boolean moveOnChessboardRequireEmpty(ChessboardCreature creature, int dstX, int dstY){
        if(!checkRange(dstX, dstY) || !isEmpty(dstX, dstY)) return false;
        removeFromChessboard(creature);
        addToChessboard(creature, dstX, dstY);

        return true;
    }

    public synchronized boolean moveOnChessboard(ChessboardCreature creature, int dstX, int dstY){
        if(!checkRange(dstX, dstY)) return false;
        removeFromChessboard(creature);
        addToChessboard(creature, dstX, dstY);
        return true;
    }

    private void setItemPosition(Item item, Grid grid){
        Rectangle2D bounds = item.getCurrentBounds();
        Point2D pos = Utils.getSettingPos(grid.getNode().getBoundsInParent(), bounds.getWidth(), bounds.getHeight());
        item.getNode().setLayoutX(pos.getX());
        item.getNode().setLayoutY(pos.getY());
    }

    private boolean checkRange(int x, int y){
        return x >= 0 && x < gameMap.getColSize() && y >= 0 && y < gameMap.getRowSize();
    }

    public synchronized boolean isEmpty(int x, int y){
        return chessboard[x][y].getCreatures().isEmpty();
    }

    public Lock getLock() {
        return lock;
    }

    public Condition getCondition() {
        return condition;
    }


    public synchronized boolean add(final Item item) {
        if(item instanceof ChessboardCreature) {
            final int x = ((ChessboardCreature) item).getX();
            final int y = ((ChessboardCreature) item).getY();
            addToChessboard((ChessboardCreature) item, x, y);


            Platform.runLater(new Runnable() {
                public void run() {
                    Grid grid = gameMap.getGridByGridPos(x, y);
                    setItemPosition(item, grid);
                    console.add(item);
                }
            });
            return true;
        }
        else return console.add(item);
    }


    public synchronized boolean addWaitRunning(final Item item) {
        if(item instanceof ChessboardCreature) {
            final int x = ((ChessboardCreature) item).getX();
            final int y = ((ChessboardCreature) item).getY();
            addToChessboard((ChessboardCreature) item, x, y);

            Platform.runLater(new Runnable() {
                public void run() {
                    Grid grid = gameMap.getGridByGridPos(x, y);
                    setItemPosition(item, grid);
                    console.addWaitRunning(item);
                }
            });

            return true;
        }
        else return console.addWaitRunning(item);
    }


    public boolean add(Task task) {
        return console.add(task);
    }


    public boolean add(Animation animation) {
        return console.add(animation);
    }


    public synchronized boolean remove(Item item) {
        if(item instanceof ChessboardCreature) {
            removeFromChessboard((ChessboardCreature) item);
            return console.remove(item);
        }
        else return console.remove(item);
    }


    public boolean remove(Task task) {
        return console.remove(task);
    }


    public boolean remove(Animation animation) {
        return console.remove(animation);
    }


    public void start() {
        console.start();
    }


    public void pause() {
        console.pause();
    }


    public void continueGame() {
        console.continueGame();
    }


    public void stop() {
        console.stop();
    }


    public GameState getGameState() {
        return console.getGameState();
    }


    public SubScene getMainScene() {
        return console.getMainScene();
    }


    public ChessboardGameMap getGameMap() {
        return gameMap;
    }


    public SubScene getGameMapScene() {
        return console.getGameMapScene();
    }


    public WorkQueue getWorkQueue() {
        return console.getWorkQueue();
    }


    public SubScene getItemScene() {
        return console.getItemScene();
    }


    public Collection<Item> getItemSet() {
        return console.getItemSet();
    }
}

class ChessboardGrid{
    private Collection<ChessboardCreature> creatures = new HashSet<ChessboardCreature>();

    public Collection<ChessboardCreature> getCreatures() {
        return creatures;
    }

    public void addCreature(ChessboardCreature creature) {
        creatures.add(creature);
    }
}

