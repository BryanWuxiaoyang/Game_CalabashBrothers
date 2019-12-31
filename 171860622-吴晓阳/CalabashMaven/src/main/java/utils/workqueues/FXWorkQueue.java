package utils.workqueues;

public class FXWorkQueue implements WorkQueue {
    private FXWorkQueue(){}

    private static final FXWorkQueue workQueue = new FXWorkQueue();

    public static FXWorkQueue getInstance(){
        return workQueue;
    }

    @Override
    public void runLater(Runnable runnable) {
        javafx.application.Platform.runLater(runnable);
    }
}
