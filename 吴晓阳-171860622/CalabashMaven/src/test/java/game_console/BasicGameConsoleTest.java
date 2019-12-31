package game_console;

import basic.basics.BasicItem;
import basic.basics.Item;
import basic.basics.ItemTypeId;
import game.homework_game.creatures.CreatureStore;
import javafx.scene.Group;
import javafx.scene.Scene;
import maps.gamemap.GameMap;
import maps.gamemap.GameMapImp1;
import maps.gamemap.GameMapImp2;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.JUnit4;
import task.Task;
import task.ThreadTask;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicGameConsoleTest{

    private static GameConsole console;

    @BeforeClass
    public static void before(){
        console = new BasicGameConsole();
    }

    @After
    public void after(){

    }

    @Test
    public void testAdd(){
        List<Item> items = new ArrayList<Item>();
        for(int i = 0; i < 100; i ++){
            items.add(new BasicItem(ItemTypeId.ITEM, null));
        }
        for (Item item : items) {
            console.getItemSet().add(item);
        }

        for (Item item : items) {
            assert(console.getItemSet().contains(item));
        }
    }

    @Test
    public void testRemove(){
        List<Item> items = new ArrayList<Item>();
        for(int i = 0; i < 100; i ++){
            items.add(new BasicItem(ItemTypeId.ITEM, null));
        }
        for (Item item : items) {
            console.getItemSet().add(item);
        }
        for(int i = 0; i < 50; i++){
            console.getItemSet().remove(items.get(i));
        }

        for(int i = 50; i < 100; i++){
            assert(console.getItemSet().contains(items.get(i)));
            assert(!console.getItemSet().contains(items.get(i - 50)));
        }
    }
}
