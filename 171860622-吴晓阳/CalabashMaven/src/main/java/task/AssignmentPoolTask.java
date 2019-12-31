package task;

import javafx.util.Duration;
import utils.Utils;

import java.util.concurrent.Callable;

public class AssignmentPoolTask implements Task {
    private Duration duration;

    private Callable<Boolean> callable;

    private Callable<Boolean> assignmentCallable;

    private int cycleCount;

    private int doneCycleCount;

    private boolean runTag = false;

    public AssignmentPoolTask(final Runnable runnable){
        this(new Callable<Boolean>() {
            public Boolean call() {
                runnable.run(); return true;
            }
        });
    }

    public AssignmentPoolTask(Callable<Boolean> callable){
        this(callable, Duration.ZERO, 1);
    }

    public AssignmentPoolTask(final Runnable runnable, Duration duration, int cycleCount){
        this(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                runnable.run(); return true;
            }
        }, duration, cycleCount);
    }

    private int getCycleCount(){
        return cycleCount;
    }

    public AssignmentPoolTask(final Callable<Boolean> callable, Duration duration, int cycleCount){
        this.callable = callable;
        this.duration = duration;
        this.cycleCount = cycleCount;
        this.doneCycleCount = 0;
        this.assignmentCallable = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                if(!runTag || (getCycleCount() != -1 && doneCycleCount >= getCycleCount())){
                    runTag = false;
                    return false;
                }

                boolean tag = callable.call();
                doneCycleCount++;

                if(!tag){
                    runTag = false;
                    return false;
                }

                return true;
            }
        };
    }

    public void run() {
        if(runTag) return;

        runTag = true;
        Utils.getAssignmentPool((int)duration.toMillis()).execute(assignmentCallable);
    }

    public void pause() {
        runTag = false;
    }

    public void stop() {
        runTag = false;
    }

    public boolean isDone() {
        return doneCycleCount >= cycleCount;
    }

    public void setWaitTermination(boolean tag) {
        throw new UnsupportedOperationException();
    }

    public boolean isWaitTermination() {
        throw new UnsupportedOperationException();
    }
}
