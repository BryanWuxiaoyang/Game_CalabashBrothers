package game.homework_game;

import basic.basics.Item;
import factory.Creator;
import factory.TimeChecker;
import game.homework_game.console.ChessboardGameConsole;
import game.homework_game.creatures.BasicCreature;
import game.homework_game.creatures.CreatureStore;
import javafx.util.Duration;
import task.AssignmentPoolTask;
import task.Task;
import task.ThreadTask;
import utils.Direction;
import utils.Utils;
import utils.workqueues.BasicWorkQueue;
import utils.workqueues.WorkQueue;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Logger {
    public enum CreatureType{
        DAWA,
        ERWA,
        SANWA,
        SIWA,
        WUWA,
        LIUWA,
        QIWA,
        YEYE,
        SNAKE,
        SCORPION,
        TOAD,
    }

    public enum RecordType{
        MOVEMENT,
        EMITTING,
        INSERTION
    }

    private List<Object> records = new ArrayList<Object>();

    public static class Movement implements Serializable {
        long time;
        int creatureId;
        int x;
        int y;
    }

    public static class Emitting implements Serializable{
        long time;
        int creatureId;
        double degree;
    }

    public static class Insertion implements Serializable{
        long time;
        CreatureType creatureType;
        int creatureId;
        int x, y;
    }

    public static class Death implements Serializable{
        long time;
        int creatureId;
    }

    public static class LifeChange implements Serializable{
        long time;
        int creatureId;
        double value;
    }

    public static class LoggedCreator implements Serializable{
        long time;
        Creator<? extends Item> creator;
    }

    private ObjectOutputStream outputStream = null;

    private TimeChecker checker = new TimeChecker(TimeUnit.MILLISECONDS);

    public Logger(){
    }

    private void closeOutputStream(){
        if(outputStream != null) {
            try {
                for(Object record:records){
                    log(record);
                }
                System.out.println("logging successfully!");
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    private void openOutputStream(String filename){
        try{
            closeOutputStream();
            outputStream = new ObjectOutputStream(new FileOutputStream(filename));
            checker.start();
        }catch (Exception e){throw new RuntimeException();}
    }

    public synchronized void startLogging(String filename){
        openOutputStream(filename);
        checker.start();
    }

    public synchronized void stopLogging(){
        closeOutputStream();
    }

    public synchronized void logMovement(BasicCreature creature, int x, int y){
        Movement movement = new Movement();
        movement.time = checker.check();
        movement.creatureId = creature.getItemId();
        movement.x = x;
        movement.y = y;

        records.add(movement);
        System.out.println("log move: " + movement.time + ", " + movement.creatureId + ", " + movement.x + ", " + movement.y);
    }

    public synchronized void logEmitting(BasicCreature creature, double degree){
        Emitting emitting = new Emitting();
        emitting.time = checker.check();
        emitting.creatureId = creature.getItemId();
        emitting.degree = degree;
        records.add(emitting);

        //System.out.println("log emit");
    }

    public synchronized void logInsertion(BasicCreature creature){
        Insertion insertion = new Insertion();
        insertion.time = checker.check();
        if(creature instanceof CreatureStore.Dawa){insertion.creatureType = CreatureType.DAWA;}
        else if(creature instanceof CreatureStore.Erwa){insertion.creatureType = CreatureType.ERWA;}
        else if(creature instanceof CreatureStore.Sanwa){insertion.creatureType = CreatureType.SANWA;}
        else if(creature instanceof CreatureStore.Siwa){insertion.creatureType = CreatureType.SIWA;}
        else if(creature instanceof CreatureStore.Wuwa){insertion.creatureType = CreatureType.WUWA;}
        else if(creature instanceof CreatureStore.Liuwa){insertion.creatureType = CreatureType.LIUWA;}
        else if(creature instanceof CreatureStore.Qiwa){insertion.creatureType = CreatureType.QIWA;}
        else if(creature instanceof CreatureStore.Yeye){insertion.creatureType = CreatureType.YEYE;}
        else if(creature instanceof CreatureStore.Snake){insertion.creatureType = CreatureType.SNAKE;}
        else if(creature instanceof CreatureStore.Scorpion){insertion.creatureType = CreatureType.SCORPION;}
        else if(creature instanceof CreatureStore.Toad){insertion.creatureType = CreatureType.TOAD;}
        else throw new RuntimeException();

        insertion.creatureId = creature.getItemId();
        insertion.x = creature.getX();
        insertion.y = creature.getY();
        records.add(insertion);

        //System.out.println("log insert");

    }

    public synchronized void logDeath(BasicCreature creature){
        Death death = new Death();
        death.time = checker.check();
        death.creatureId = creature.getItemId();

        records.add(death);
        //System.out.println("log death");
    }

    public synchronized void logLifeChange(BasicCreature creature, double value){
        LifeChange lifeChange = new LifeChange();
        lifeChange.time = checker.check();
        lifeChange.creatureId = creature.getItemId();
        lifeChange.value = value;

        records.add(lifeChange);
        //System.out.println("log life change");
    }

    public synchronized void logCreator(Creator<? extends Item> creator){
        LoggedCreator loggedCreator = new LoggedCreator();
        loggedCreator.time = checker.check();
        loggedCreator.creator = creator;

        records.add(loggedCreator);
        //System.out.println("log creator");
    }

    private void log(Object object){
        try{
           if(outputStream != null) {
               if(!(object instanceof Serializable)){
                   System.out.println(object);
                   assert false;
               }
               outputStream.writeObject(object);
           }
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private boolean loadingTag = false;

    public synchronized boolean isLoading(){
        return loadingTag;
    }

    public synchronized void startLoading(File file, ChessboardGameConsole console){
        loadingTag = true;
        console.start();

        final TimeChecker currentChecker = new TimeChecker(TimeUnit.MILLISECONDS);
        currentChecker.start();
        final Queue<Movement> movements = new ConcurrentLinkedQueue<Movement>();
        final Map<Integer, BasicCreature> creatureMap = Collections.synchronizedMap(new HashMap<Integer, BasicCreature>());

        Task movementTask = new ThreadTask(new Runnable() {
            public void run() {
                Movement movement = movements.poll();

                if(movement != null){
                    double remainTime = movement.time - currentChecker.check();
                    if(remainTime > 0) Utils.sleep((int)remainTime);
                    BasicCreature creature = creatureMap.get(movement.creatureId);
                    assert creature != null;
                    creature.moveTo(movement.x, movement.y, false);
                }
            }
        }, Duration.millis(500), -1);
        //movementTask.run();

        List<Object> inputRecords = null;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            Object tmpObject;
            inputRecords = new ArrayList<Object>();
            while ((tmpObject = inputStream.readObject()) != null) {
                inputRecords.add(tmpObject);
            }
        }catch (Exception ignore){}
        assert inputRecords != null;

        for(Object object : inputRecords){
            //Utils.sleep(10);
            if(object instanceof Insertion){
                Insertion insertion = (Insertion) object;
                while(insertion.time -currentChecker.check() > 0)Utils.sleep(5);

                BasicCreature creature = null;
                switch(insertion.creatureType){
                    case DAWA:  creature = CreatureStore.getCalabash(0);    break;
                    case ERWA:  creature = CreatureStore.getCalabash(1);    break;
                    case SANWA: creature = CreatureStore.getCalabash(2);    break;
                    case SIWA:  creature = CreatureStore.getCalabash(3);    break;
                    case WUWA:  creature = CreatureStore.getCalabash(4);    break;
                    case LIUWA: creature = CreatureStore.getCalabash(5);    break;
                    case QIWA:  creature = CreatureStore.getCalabash(6);    break;
                    case YEYE:  creature = CreatureStore.getYeye(); break;
                    case SNAKE: creature = CreatureStore.getEnemy(0); break;
                    case SCORPION: creature = CreatureStore.getEnemy(1); break;
                    case TOAD:  creature = CreatureStore.getEnemy(2); break;
                }

                creature.setX(insertion.x);
                creature.setY(insertion.y);
                creature.setConsole(console);
                creature.setDamage(0);
                creature.setOnDeath(null);
                creature.setLoading(true);

                creatureMap.put(insertion.creatureId, creature);
                console.add(creature);

                //System.out.println("insertion :" +  insertion.time + ", " + insertion.creatureId + ", " + insertion.creatureType);
            }
            else if(object instanceof Emitting){
                Emitting emitting = (Emitting) object;
                while(emitting.time -currentChecker.check() > 0)Utils.sleep(5);

                BasicCreature creature = creatureMap.get(emitting.creatureId);
                assert creature != null;

                creature.setDegree(emitting.degree);
                creature.emit();
                //System.out.println("emitting: " + emitting.time + ", " + emitting.creatureId + ", " + emitting.degree);
            }
            else if(object instanceof Movement){
                //Utils.sleep(500);
                Movement movement = (Movement) object;
                //movements.offer(movement);
                while(movement.time -currentChecker.check() > 0) Utils.sleep(100);
                BasicCreature creature = creatureMap.get(movement.creatureId);
                assert creature != null;
                boolean suc = creature.moveTo(movement.x, movement.y, true);

                //System.out.println("movement: " + movement.time + ", " + movement.creatureId + ", " + movement.x + ", " + movement.y + ", " + suc);
                if(!suc){
                    System.out.println("movement: " + movement.time + ", " + movement.creatureId + ", " + movement.x + ", " + movement.y + ", " + suc);
                }
            }
            else if(object instanceof Death){
                Death death = (Death) object;
                while(death.time -currentChecker.check() > 0)Utils.sleep(5);

                BasicCreature creature = creatureMap.get(death.creatureId);
                assert creature != null;

                //if(!creature.isRemoved()) System.out.println("death: " + creature);
                creature.setRemoved(true);
            }
            else if(object instanceof LifeChange){
                LifeChange lifeChange = (LifeChange) object;
                while(lifeChange.time -currentChecker.check() > 0) Utils.sleep(5);

                BasicCreature creature = creatureMap.get(lifeChange.creatureId);
                assert creature != null;

                creature.increaseLife(lifeChange.value);

                //System.out.println("life change" + creature);
            }
            else if(object instanceof LoggedCreator){
                LoggedCreator loggedCreator = (LoggedCreator) object;
                while(loggedCreator.time -currentChecker.check() > 0)Utils.sleep(5);

                Item item = loggedCreator.creator.create();
                item.setConsole(console);
                console.add(item);
            }
        }

        movementTask.stop();
        loadingTag = false;
    }
}
