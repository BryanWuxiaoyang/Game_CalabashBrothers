package task;

import javafx.util.Duration;
import utils.Utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadTask implements Task {
    private Duration duration;

    private int cycleCount;

    private int doneCycleCount;

    private Runnable threadRunnable;

    private ExecutorService executorService = Utils.getThreadPool();

    private boolean stopTag = false;

    private boolean runningTag = false;

    private boolean waitTermination = false;

    public ThreadTask(Runnable runnable){
        this(runnable, Duration.ZERO, 1);
    }

    public ThreadTask(final Runnable runnable, Duration duration, int cycleCount){
        this(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                runnable.run(); return true;
            }
        }, duration, cycleCount);
    }

    public ThreadTask(final Callable<Boolean> callable, final Duration duration, final int cycleCount){
        this.duration = duration;
        this.cycleCount = cycleCount;
        this.doneCycleCount = 0;
        this.threadRunnable = new Runnable() {
            public void run() {
                runningTag = true;
                int sleepTime = (int)duration.toMillis();
                while(!stopTag && (cycleCount == -1 || doneCycleCount < cycleCount)){
                    if(sleepTime > 0) try{Thread.sleep(sleepTime);} catch (InterruptedException ignore){}
                    boolean tag;
                    try{tag = callable.call();}catch (Exception ignore){throw new RuntimeException();}
                    ++doneCycleCount;
                    if(!tag) break;
                }
                runningTag = false;
            }
        };
    }

    public Duration getDuration() {
        return duration;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public int getDoneCycleCount() {
        return doneCycleCount;
    }


    public void run() {
        stopTag = false;
        if(runningTag) return;

        runningTag = true;
        executorService.execute(threadRunnable);

        if(waitTermination){
            try {
                executorService.awaitTermination(1000000, TimeUnit.SECONDS);
            }catch (InterruptedException e){

            } catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }


    public void pause() {
        stopTag = true;
    }


    public void stop() {
        stopTag = true;
    }


    public boolean isDone() {
        return doneCycleCount >= cycleCount;
    }


    public void setWaitTermination(boolean tag) {
        waitTermination = tag;
    }


    public boolean isWaitTermination() {
        return waitTermination;
    }
}
