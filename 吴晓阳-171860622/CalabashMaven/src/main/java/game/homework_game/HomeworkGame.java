package game.homework_game;

import basic.basics.Item;
import basic.traits.EmitEvent;
import basic.traits.LifeChangeEvent;
import basic.traits.RemoveEvent;
import factory.Creator;
import game.homework_game.console.ChessboardGameConsole;
import game.homework_game.creatures.*;
import game.homework_game.effects.Catastrophe;
import game_console.BasicGameConsole;
import game_console.GameConsole;
import game_console.InteractionGameConsole;
import game_console.OverloadCheckGameConsole;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import maps.gamemap.ChessboardGameMap;
import maps.gamemap.GameMapImp2;
import maps.grids.Grid;
import task.AssignmentPoolTask;
import task.Task;
import task.TimelineTask;
import utils.Direction;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeworkGame {
    private ChessboardGameConsole console;

    private Logger logger = new Logger();

    private ChessboardGameMap gameMap;

    private final BorderPane mainPane;

    private final Scene scene;

    private List<BasicCreature> defenders;

    private List<BasicCreature> enemies;

    private Task task1;

    private Task task2;

    private Task task3;

    private boolean loggingState = false;

    private void init(){
        if(loggingState){
            logger.stopLogging();
            logger.startLogging("log");
        }

        this.gameMap = new GameMapImp2();
        this.console =
                new ChessboardGameConsole(
                        new InteractionGameConsole(
                                new BasicGameConsole(gameMap),
                                true
                        )
                );
        this.mainPane.setCenter(console.getMainScene());
    }

    public HomeworkGame(){
        this.mainPane = new BorderPane();
        this.scene = new Scene(mainPane);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                ChessboardGameConsole curConsole = getConsole();
                if (curConsole != null) curConsole.getLock().lock();
                switch (event.getCode()) {
                    case W:
                        if (yeye != null && !yeye.isRemoved()) yeye.moveUp();
                        break;
                    case S:
                        if (yeye != null && !yeye.isRemoved()) yeye.moveDown();
                        break;
                    case A:
                        if (yeye != null && !yeye.isRemoved()) yeye.moveLeft();
                        break;
                    case D:
                        if (yeye != null && !yeye.isRemoved()) yeye.moveRight();
                        break;
                    case F:
                        if (yeye != null && !yeye.isRemoved()) yeye.emit();
                        break;
                    case SPACE:
                        HomeworkGame.this.start();
                        break;
                    case L:
                        Stage fileStage = new Stage();
                        FileChooser fileChooser = new FileChooser();
                        File file = fileChooser.showOpenDialog(fileStage);
                        if(file !=null) startLoading(file);
                        break;
                }
                if (curConsole != null) curConsole.getLock().unlock();
            }
        });
        init();
    }

    private BasicCreature yeye = null;

    private int[][] calabashPositions = new int[7][];{
        calabashPositions[0] = new int[]{10, 19};
        calabashPositions[1] = new int[]{10, 18};
        calabashPositions[2] = new int[]{10, 17};
        calabashPositions[3] = new int[]{10, 16};
        calabashPositions[4] = new int[]{10, 15};
        calabashPositions[5] = new int[]{10, 14};
        calabashPositions[6] = new int[]{10, 13};
    }

    private int[][] enemyPositions = new int[8][];{
        enemyPositions[0] = new int[]{1, 1};
        enemyPositions[1] = new int[]{3, 3};
        enemyPositions[2] = new int[]{4, 6};
        enemyPositions[3] = new int[]{6, 8};
        enemyPositions[4] = new int[]{8, 9};
        enemyPositions[5] = new int[]{10, 7};
        enemyPositions[6] = new int[]{13, 4};
        enemyPositions[7] = new int[]{17, 0};
    }

    private int[] yeyePosition = new int[]{4, 19};

    private void setCreatures(){
        defenders = Collections.synchronizedList(new ArrayList<BasicCreature>());
        enemies = Collections.synchronizedList(new ArrayList<BasicCreature>());

        for(int i = 0; i < calabashPositions.length; i++){
            final Calabash calabash = CreatureStore.getCalabash(i);
            calabash.setX(calabashPositions[i][0]);
            calabash.setY(calabashPositions[i][1]);
            calabash.setConsole(console);
            defenders.add(calabash);
            console.add(calabash);

            if(loggingState) {
                logger.logInsertion(calabash);
                calabash.setOnEmit(new EventHandler<EmitEvent>() {
                    public void handle(EmitEvent event) {
                        if (loggingState) logger.logEmitting(calabash, calabash.getDegree());
                    }
                });
                calabash.setOnMove(new EventHandler<MoveEvent>() {
                    public void handle(MoveEvent event) {
                        if (loggingState) logger.logMovement(calabash, event.getX(), event.getY());
                    }
                });
                calabash.setOnRemove(new EventHandler<RemoveEvent>() {
                    public void handle(RemoveEvent event) {
                        console.remove(calabash);
                        console.add(new DeadCreature(calabash.getImage(), calabash.getX(), calabash.getY()));
                        if (loggingState) logger.logDeath(calabash);
                    }
                });
                calabash.setOnLifeChanged(new EventHandler<LifeChangeEvent>() {
                    public void handle(LifeChangeEvent event) {
                        if (loggingState) logger.logLifeChange(calabash, event.getDstLife() - event.getSrcLife());
                    }
                });
            }
        }

        for(int i = 0; i < enemyPositions.length; i++){
            final Enemy enemy = CreatureStore.getEnemy(i);
            enemy.setX(enemyPositions[i][0]);
            enemy.setY(enemyPositions[i][1]);
            enemy.setConsole(console);
            enemies.add(enemy);
            console.add(enemy);

            if(loggingState) {
                logger.logInsertion(enemy);
                enemy.setOnEmit(new EventHandler<EmitEvent>() {
                    public void handle(EmitEvent event) {
                        if (loggingState) logger.logEmitting(enemy, enemy.getDegree());
                    }
                });
                enemy.setOnMove(new EventHandler<MoveEvent>() {
                    public void handle(MoveEvent event) {
                        if (loggingState) logger.logMovement(enemy, event.getX(), event.getY());
                    }
                });
                enemy.setOnRemove(new EventHandler<RemoveEvent>() {
                    public void handle(RemoveEvent event) {
                        console.remove(enemy);
                        console.add(new DeadCreature(enemy.getImage(), enemy.getX(), enemy.getY()));
                        if (loggingState) logger.logDeath(enemy);
                    }
                });
                enemy.setOnLifeChanged(new EventHandler<LifeChangeEvent>() {
                    public void handle(LifeChangeEvent event) {
                        if (loggingState) logger.logLifeChange(enemy, event.getDstLife() - event.getSrcLife());
                    }
                });
            }
        }

        yeye = CreatureStore.getYeye();
        yeye.setX(yeyePosition[0]);
        yeye.setY(yeyePosition[1]);
        yeye.setConsole(console);
        defenders.add(yeye);
        console.add(yeye);

        if(loggingState){
            logger.logInsertion(yeye);
            yeye.setOnEmit(new EventHandler<EmitEvent>() {
                public void handle(EmitEvent event) {
                    if (loggingState) logger.logEmitting(yeye, yeye.getDegree());
                }
            });
            yeye.setOnMove(new EventHandler<MoveEvent>() {
                public void handle(MoveEvent event) {
                    if (loggingState) logger.logMovement(yeye, event.getX(), event.getY());
                }
            });
            yeye.setOnRemove(new EventHandler<RemoveEvent>() {
                public void handle(RemoveEvent event) {
                    console.remove(yeye);
                    console.add(new DeadCreature(yeye.getImage(), yeye.getX(), yeye.getY()));
                    if (loggingState) logger.logDeath(yeye);
                }
            });
            yeye.setOnLifeChanged(new EventHandler<LifeChangeEvent>() {
                public void handle(LifeChangeEvent event) {
                    if (loggingState) logger.logLifeChange(yeye, event.getDstLife() - event.getSrcLife());
                }
            });
        }
    }

    public Scene getScene(){
        return scene;
    }

    public ChessboardGameConsole getConsole(){
        return console;
    }

    private static class CatastrophyCreator implements Creator<Item>{
        private transient GameConsole console;

        private final double x;

        private final double y;

        private final double width;

        private final double height;

        public CatastrophyCreator(GameConsole console, double x, double y, double width, double height) {
            this.console = console;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Item create() {
            return new Catastrophe(console, x, y, width, height);
        }
    }
    public void start(){
        loggingState = true;
        init();

        setCreatures();
        console.start();
        task1 = new AssignmentPoolTask(new Runnable() {
            public void run() {
                for (BasicCreature calabash : defenders) {
                    if (calabash.getTarget() == null) {
                        BasicCreature enemy = enemies.get(Utils.getRandom(enemies.size()));
                        calabash.setTarget(enemy);
                        Utils.sleep(Utils.getRandom(100));
                    }
                }
                for (BasicCreature enemy : enemies) {
                    if (enemy.getTarget() == null) {
                        BasicCreature defender = defenders.get(Utils.getRandom(defenders.size()));
                        enemy.setTarget(defender);
                        Utils.sleep(Utils.getRandom(100));
                    }
                }
            }
        }, Duration.millis(500), -1);

        task1.run();

        task2 = new AssignmentPoolTask(new Runnable() {
            public void run() {
                int gridX = Utils.getRandom(gameMap.getColSize());
                int gridY = Utils.getRandom(gameMap.getRowSize());
                Grid grid = gameMap.getGridByGridPos(gridX, gridY);
                Bounds bounds = grid.getNode().getBoundsInParent();
                final double x = bounds.getMinX() - bounds.getWidth();
                final double y = bounds.getMinY() - bounds.getHeight();
                final double width = bounds.getWidth() * 2;
                final double height = bounds.getHeight() * 2;

                Catastrophe catastrophe = new Catastrophe(console, x, y, width, height);

                console.add(catastrophe);
                if(loggingState) logger.logCreator(new CatastrophyCreator(null, x, y, width, height));
            }
        }, Duration.millis(1000), -1);

        task2.run();

        task3 = new AssignmentPoolTask(new Runnable() {
            public void run() {
                boolean resume = false;
                for(BasicCreature creature : defenders){
                    if(!creature.isRemoved()){
                        resume = true;
                        break;
                    }
                }

                if(!resume){
                    HomeworkGame.this.stop();
                    return;
                }

                resume = false;
                for(BasicCreature creature : enemies){
                    if(!creature.isRemoved()){
                        resume = true;
                        break;
                    }
                }

                if(!resume) {
                    HomeworkGame.this.stop();
                }
            }
        }, Duration.millis(500), -1);

        task3.run();
    }

    public void stop(){
        System.out.println("stoped");
        if(task1 != null) task1.stop();
        if(task2 != null) task2.stop();
        if(task3 != null) task3.stop();
        task1 = task2 = task3 = null;
        if(loggingState){
            console.pause();
            logger.stopLogging();
        }
    }

    public void startLoading(final File file){
        loggingState = false;
        init();

        task1 = new AssignmentPoolTask(new Runnable() {
            public void run() {
                if(!logger.isLoading()){
                    stop();
                }
            }
        }, Duration.millis(500), -1);
        task1.run();

        Utils.getThreadPool().execute(new Runnable() {
            public void run() {
                logger.startLoading(file, console);
            }
        });
    }
}
