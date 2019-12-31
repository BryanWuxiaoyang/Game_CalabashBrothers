package utils;

import factory.TimeChecker;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class AssignmentPool {
    private Queue<Callable<Boolean>> assignments = new ConcurrentLinkedQueue<Callable<Boolean>>();

    private long intervalMillis;

    private boolean runTag = false;

    private boolean overLoadTag = false;
    
    private Runnable runnable = new Runnable() {
        public void run() {
            TimeChecker timeChecker = new TimeChecker(TimeUnit.MILLISECONDS);
            while(true){
                timeChecker.start();

                List<Callable<Boolean>> removeSet = new ArrayList<Callable<Boolean>>();
                for(Callable<Boolean> assignment: assignments){
                    try{
                        if(!assignment.call()) removeSet.add(assignment);
                    }catch (Exception e){
                        runTag = false; e.printStackTrace(); throw new RuntimeException();}
                }


                assignments.removeAll(removeSet);
                if(assignments.isEmpty()) break;

                long usedTime = timeChecker.check();
                long remain = intervalMillis - usedTime;
                if(remain > 0) {
                    overLoadTag = false;
                    Utils.sleep(remain);
                }
                else {
                    overLoadTag = true;
                }
            }

            runTag = false;
        }
    };

    public AssignmentPool(Duration interval){
        this.intervalMillis = (long)interval.toMillis();
    }
    
    public void execute(Callable<Boolean> assignment){
        assignments.offer(assignment);
        if(!runTag) {
            runTag = true;
            Utils.getThreadPool().execute(runnable);
        }
    }

    public int getAssignmentNum(){
        return assignments.size();
    }

    public boolean isOverLoaded(){
        return overLoadTag;
    }
}
