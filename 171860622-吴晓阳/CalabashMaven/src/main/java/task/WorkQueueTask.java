package task;

import javafx.application.Platform;
import javafx.util.Duration;
import utils.workqueues.WorkQueue;

public class WorkQueueTask extends ThreadTask{
    public WorkQueueTask(final Runnable runnable, final WorkQueue workQueue){
        super(new Runnable() {
            public void run() {
                workQueue.runLater(runnable);
            }
        });
    }

    public WorkQueueTask(final Runnable runnable, Duration duration, int cycleCount, final WorkQueue workQueue){
        super(new Runnable() {
            public void run() {
                workQueue.runLater(runnable);
            }
        }, duration, cycleCount);
    }
}
