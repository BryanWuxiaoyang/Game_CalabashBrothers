package main;

import basic.basics.Item;
import game.homework_game.HomeworkGame;
import game.homework_game.Logger;
import game.homework_game.console.ChessboardGameConsole;
import game.homework_game.creatures.Calabash;
import game.homework_game.creatures.CreatureStore;
import game.towerdefence_game.TowerDefenceGame;
import game.towerdefence_game.creatures.Archer;
import game.towerdefence_game.creatures.BaseCreature;
import game.towerdefence_game.creatures.EnemyCreature;
import game.towerdefence_game.traits.Base;
import game.towerdefence_game.traits.Defender;
import game.towerdefence_game.traits.Enemy;
import game_console.BasicGameConsole;
import game_console.GameConsole;
import game_console.InteractionGameConsole;
import game_console.OverloadCheckGameConsole;
import game_systems.shop.Commodity;
import game_systems.shop.MoneyPocket;
import game_systems.shop.Shop;
import factory.BasicCreator;
import factory.Creator;
import factory.TimingCreator;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import maps.GridType;
import maps.gamemap.ChessboardGameMap;
import maps.gamemap.GameMapImp1;
import maps.gamemap.GameMapImp2;
import maps.grids.ChessboardGrid;
import task.ThreadTask;
import utils.Direction;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private void startTowerDefenceGame(Stage primaryStage){
        Creator<Enemy> enemyCreator1 =  new TimingCreator<Enemy>(new BasicCreator<Enemy>(EnemyCreature.class), 100, TimeUnit.MILLISECONDS);

        Creator<Enemy> enemyCreator2 = new TimingCreator<Enemy>(new BasicCreator<Enemy>(EnemyCreature.class), 5000, TimeUnit.MILLISECONDS);

        List<Creator<Enemy>> enemyCreators = new ArrayList<Creator<Enemy>>();
        Collections.addAll(enemyCreators, enemyCreator1);


        Creator<Defender> defenderCreator = new BasicCreator<Defender>(Archer.class);
        Creator<Base> baseCreator = new BasicCreator<Base>(BaseCreature.class);

        GameMapImp1 gameMap = new GameMapImp1();
        GameConsole console1 = new BasicGameConsole(gameMap);
        GameConsole console2 = new InteractionGameConsole(console1, true);
        GameConsole console3 = new OverloadCheckGameConsole(console2, 4000);

        Shop shop = new Shop("商城", 3, 3);

        final Image[] images = new Image[]{
                new Image("dawa.jpg", 50, 50, true, true),
                new Image("erwa.jpg", 50, 50, false, true),
                new Image("sanwa.jpg", 50, 50, false, true),
                new Image("siwa.jpg", 50, 50, false, true),
                new Image("wuwa.jpg", 50, 50, false, true),
                new Image("liuwa.jpg", 50, 50, false, true),
                new Image("qiwa.jpg", 50, 50, false, true)
        };

        for(int i = 0; i < 7; i++) {
            final int index = i;
            Commodity commodity = new Commodity(new Creator<Item>() {
                public Item create() {
                    return new Archer(images[index]);
                }
            }, "defender " + i, new ImageView(images[i]), 100, 100);
            shop.add(commodity);
        }

        MoneyPocket moneyPocket = new MoneyPocket(500000);
        shop.setMoneyPocket(moneyPocket);

        TowerDefenceGame game = new TowerDefenceGame(console3, enemyCreators, baseCreator, defenderCreator, shop);

        PerspectiveCamera camera1 = new PerspectiveCamera(false);
        camera1.getTransforms().addAll(new Rotate(45, Rotate.X_AXIS));
        //game.getConsole().getMainScene().setCamera(camera1);

        primaryStage.setScene(game.getScene());
        primaryStage.show();
        primaryStage.setAlwaysOnTop(true);
    }

    private void startHomeworkGame(Stage primaryStage){
        HomeworkGame game = new HomeworkGame();
        //game.start();
        primaryStage.setScene(game.getScene());
        primaryStage.show();
    }

    private void logTest(Stage primaryStage){
        final Logger logger = new Logger();
        logger.startLogging("log");
        Calabash[] calabashes = new Calabash[7];
        for(int i = 0; i < 1; i++){
            calabashes[i] = CreatureStore.getCalabash(i);
            calabashes[i].setX(10);
            calabashes[i].setY(19);
            logger.logInsertion(calabashes[i]);
            Utils.sleep(100);
        }

        for(int i = 0; i < 10; i++){
            logger.logEmitting(calabashes[0], i);
            Utils.sleep(500);
        }

        logger.stopLogging();

        ChessboardGameMap gameMap = new GameMapImp2();
        final ChessboardGameConsole gameConsole = new ChessboardGameConsole(new InteractionGameConsole(new BasicGameConsole(gameMap), true));
        gameConsole.start();

        Utils.getThreadPool().execute(new Runnable() {
            public void run() {
                Utils.sleep(5000);
                logger.startLoading(new File("log"), gameConsole);
            }
        });

        Group group = new Group(gameConsole.getMainScene());
        Scene scene = new Scene(group);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage){
        startHomeworkGame(primaryStage);
        //startTowerDefenceGame(primaryStage);
        //logTest(primaryStage);
        //primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
