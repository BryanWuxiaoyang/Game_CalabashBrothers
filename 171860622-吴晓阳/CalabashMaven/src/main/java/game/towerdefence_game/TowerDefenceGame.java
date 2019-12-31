package game.towerdefence_game;

import basic.traits.Emitter;
import basic.traits.RemoveEvent;
import game.homework_game.console.ChessboardGameConsole;
import game.towerdefence_game.creatures.Archer;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import maps.gamemap.ChessboardGameMap;
import maps.grids.ChessboardGrid;
import path_generation.GridPathCreator;
import path_generation.MapPath;
import path_generation.MapPathCreator;
import game.towerdefence_game.traits.Defender;
import game_console.GameConsole;
import game_systems.shop.Shop;
import game.towerdefence_game.traits.Enemy;
import factory.Creator;
import game.towerdefence_game.traits.Base;
import basic.traits.HasLife;
import basic.traits.HasMove;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import path_generation.Route;
import task.Task;
import task.ThreadTask;
import task.TimelineTask;
import utils.Utils;
import javafx.animation.PathTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import maps.gamemap.GameMap;
import maps.grids.Grid;
import maps.GridType;
import game_console.GameState;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class TowerDefenceGame {
    private List<Creator<Enemy>> enemyCreators;

    private Creator<Base> baseCreator;

    private Creator<Defender> defenderCreator;

    private GameConsole console;

    private Shop shop;

    Button startButton = new Button("start");
    {
        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (console.getGameState() == GameState.STOP) {
                    TowerDefenceGame.this.start();
                }
            }
        });
    }

    Button stopButton = new Button("stop");
    {
        stopButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (console.getGameState() == GameState.RUNNING || console.getGameState() == GameState.PAUSE) {
                    end();
                }
            }
        });

    }

    Button continueButton = new Button("continue");
    {
        continueButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (console.getGameState() == GameState.PAUSE) {
                    console.continueGame();
                }
            }
        });
    }

    Button pauseButton = new Button("pause");
    {
        pauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (console.getGameState() == GameState.RUNNING) {
                    console.pause();
                }
            }
        });
    }

    private void setPhaseLabel(final String text){
        new TimelineTask(new Runnable() {
            public void run() {
                phaseLabel.setText(text);
            }
        }).run();
    }

    Label phaseLabel = new Label("准备游戏");{
        phaseLabel.setAlignment(Pos.CENTER);
    }

    Task phaseLabelTask = new TimelineTask(new Runnable() {
        public void run() {

        }
    }, Duration.millis(1000), -1);

    private int curEnemyCreatorIndex = 0;

    private HBox controlBox = new HBox(startButton, stopButton, continueButton, pauseButton, phaseLabel);

    private BorderPane shopPane = new BorderPane();

    private BorderPane mainPane;

    private Scene scene;

    public TowerDefenceGame(GameConsole console,
                            List<Creator<Enemy>> enemyCreators,
                            Creator<Base> baseCreator,
                            Creator<Defender> defenderCreator,
                            Shop shop){
        this.enemyCreators = enemyCreators;
        this.baseCreator = baseCreator;
        this.defenderCreator = defenderCreator;
        this.console = console;
        this.shop = shop;
        this.shopPane.setCenter(shop);
        this.mainPane = new BorderPane(console.getMainScene(), controlBox, null, null, shopPane);
        this.scene = new Scene(mainPane);

        this.phaseLabelTask.run();
        this.aliveEnemyCheckTask.run();

        this.console.getMainScene().setCamera(new PerspectiveCamera(false));
        this.mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                GameConsole console1 = getConsole();
                switch (event.getCode()) {
                    case A:
                        console1.getMainScene().getCamera().setTranslateX(console1.getMainScene().getCamera().getTranslateX() - 100);
                        break;
                    case D:
                        console1.getMainScene().getCamera().setTranslateX(console1.getMainScene().getCamera().getTranslateX() + 100);
                        break;
                    case W:
                        console1.getMainScene().getCamera().setTranslateY(console1.getMainScene().getCamera().getTranslateY() - 100);
                        break;
                    case S:
                        console1.getMainScene().getCamera().setTranslateY(console1.getMainScene().getCamera().getTranslateY() + 100);
                        break;
                }
            }
        });
        setBaseItems();
        setEnemyGeneration();
        setDefenderItems();

        setPhaseLabel("点击start开始游戏");
    }

    public Shop getShop() {
        return shop;
    }

    public Scene getScene(){
        return scene;
    }

    private void setBaseItems(){
        Collection<Grid> dstSet = console.getGameMap().getEnemyDst();
        for(final Grid dstGrid: dstSet){
            final HasLife creature = baseCreator.create();
            assert(creature != null);

            creature.setOnDeath(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    end();
                }
            });
            creature.setOnRemove(new EventHandler<RemoveEvent>() {
                public void handle(RemoveEvent event) {
                    console.remove(creature);
                }
            });
            Platform.runLater(new Runnable() {
                public void run() {
                    Bounds bounds = creature.getNode().getBoundsInLocal();
                    Point2D pos = Utils.getSettingPos(dstGrid.getNode().getBoundsInParent(), bounds.getWidth(), bounds.getHeight());
                    creature.getNode().setLayoutX(pos.getX());
                    creature.getNode().setLayoutY(pos.getY());
                    console.add(creature);
                }
            });
        }
    }

    private Collection<Enemy> aliveEnemies = new CopyOnWriteArraySet<Enemy>();

    private Task aliveEnemyCheckTask = new ThreadTask(new Runnable() {
        public void run() {
            Collection<Enemy> removeSet = new ArrayList<Enemy>();
            for(Enemy enemy: aliveEnemies){
                if(enemy.isRemoved()) removeSet.add(enemy);
            }

            aliveEnemies.removeAll(removeSet);

            if((curEnemyCreatorIndex == enemyCreators.size()) && aliveEnemies.isEmpty()) {
                end();
            }
        }
    }, Duration.millis(1000), -1);

    private void setEnemyGeneration(){
        curEnemyCreatorIndex = 0;
        setPhaseLabel("阶段： " + (curEnemyCreatorIndex + 1) + "/" + enemyCreators.size());

        GameMap gameMap = console.getGameMap();

        final GridPathCreator gridPathCreator = new GridPathCreator(gameMap.getEnemySrc(), gameMap.getEnemyDst());
        final List<Route> routes = gridPathCreator.create();
        final Route route = routes.get(Utils.getRandom(routes.size()));

        Task enemyCreationTask = new ThreadTask(new Runnable() {
            public void run() {
                Enemy tryEnemy = enemyCreators.get(curEnemyCreatorIndex).create();
                while(tryEnemy == null){
                    curEnemyCreatorIndex++;
                    if(curEnemyCreatorIndex == enemyCreators.size()){
                        return;
                    }
                    else{
                        setPhaseLabel("阶段： " + (curEnemyCreatorIndex + 1) + "/" + enemyCreators.size());
                        try{Thread.sleep(5000);}catch (InterruptedException ignore){}
                    }
                    tryEnemy = enemyCreators.get(curEnemyCreatorIndex).create();
                }

                final Iterator<Grid> iterator = route.iterator();
                final Point2D srcGridCenter = Utils.getCenter(route.getSrcGrid().getNode().getBoundsInParent());
                final MapPath mapPath = new MapPathCreator(iterator, srcGridCenter.getX(), srcGridCenter.getY()).create();

                final Enemy enemy = tryEnemy;
                enemy.setConsole(console);
                enemy.getNode().setLayoutX(srcGridCenter.getX());
                enemy.getNode().setLayoutY(srcGridCenter.getY());

                PathTransition transition = new PathTransition();
                transition.setPath(mapPath.getPath());
                transition.setNode(enemy.getNode());
                transition.setDuration(Duration.seconds(mapPath.getTotalLength() / ((HasMove) enemy).getSpeed()));
                transition.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        enemy.setRemoved(true);
                    }
                });
                enemy.getAnimations().add(transition);

                aliveEnemies.add(enemy);
                console.addWaitRunning(enemy);

            }
        }, Duration.ZERO, -1);

        console.add(enemyCreationTask);
    }

    private void setDefenderItems(){
        GameMap gameMap = console.getGameMap();

        Collection<Grid> grids = gameMap.getGrids();
        for(final Grid grid: grids){
            if(grid.getType() == GridType.STAND){
                grid.getNode().setOnMouseClicked(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        final Defender defender;
                        if (shop == null) {
                            defender = defenderCreator.create();
                        } else {
                            defender = ((Defender) shop.purchase());
                        }
                        if (defender != null) {
                            defender.setConsole(console);

                            defender.saveCurrentBounds();
                            Bounds bounds = defender.getNode().getBoundsInLocal();
                            Point2D pos = Utils.getSettingPos(grid.getNode().getBoundsInParent(), bounds.getWidth(), bounds.getHeight());
                            defender.getNode().setLayoutX(pos.getX());
                            defender.getNode().setLayoutY(pos.getY());

                            defender.getNode().setOnMouseClicked(new EventHandler<MouseEvent>() {
                                private boolean clicked = false;
                                private Rectangle shape;

                                public void handle(MouseEvent event) {
                                    clicked = !clicked;
                                    System.out.println(clicked);
                                    if (clicked) {
                                        Rectangle2D rectangle2D = defender.getInteractingRange();
                                        shape = new Rectangle(rectangle2D.getMinX(), rectangle2D.getMinY(), rectangle2D.getWidth(), rectangle2D.getHeight());
                                        shape.setFill(Color.GOLD);
                                        Platform.runLater(new Runnable() {
                                            public void run() {
                                                console.getGameMap().getNode().getChildren().add(shape);
                                            }
                                        });
                                    } else {
                                        Platform.runLater(new Runnable() {
                                            public void run() {
                                                console.getGameMap().getNode().getChildren().remove(shape);
                                            }
                                        });
                                    }
                                }
                            });

                            console.add(defender);
                        }
                    }
                });
            }
        }

        ChessboardGameMap map = (ChessboardGameMap) gameMap;
        for(int i = 0; i < map.getColSize(); i++){
            for(int j = 0; j < map.getRowSize(); j++){
                ChessboardGrid grid = map.getGridByGridPos(i, j);
                if(grid != null && grid.getType() == GridType.STAND){
                    Defender defender = new Archer();
                    Point2D pos = Utils.getSettingPos(grid.getNode().getBoundsInParent(), defender.getNode().getLayoutBounds().getWidth(), defender.getNode().getLayoutBounds().getHeight());
                    defender.getNode().setLayoutX(pos.getX());
                    defender.getNode().setLayoutY(pos.getY());
                    defender.setConsole(console);
                    console.add(defender);
                }
            }
        }
    }

    public GameConsole getConsole(){
        return console;
    }

    public void start(){
        setPhaseLabel("阶段： " + (curEnemyCreatorIndex + 1) + "/" + enemyCreators.size());
        console.start();
    }

    public void end(){
        console.stop();
        phaseLabelTask.stop();
        aliveEnemyCheckTask.stop();

        setPhaseLabel("游戏结束");
    }
}
