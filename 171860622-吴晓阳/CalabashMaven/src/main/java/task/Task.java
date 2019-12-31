package task;

import java.util.concurrent.Callable;

public interface Task {
    void run();

    void pause();

    void stop();

    boolean isDone();

    void setWaitTermination(boolean tag);

    boolean isWaitTermination();
}
