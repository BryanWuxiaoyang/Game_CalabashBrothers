package utils.workqueues;

public interface WorkQueue {
    void runLater(Runnable runnable);
}
