package task;

import java.time.Duration;

public class DelayedRunnable implements Runnable{
    private Runnable runnable;

    private Duration delay;

    public DelayedRunnable(Runnable runnable, Duration delay){
        this.runnable = runnable;
        this.delay = delay;
    }

    @Override
    public void run() {
        try{Thread.sleep((int)delay.toMillis());}catch (InterruptedException ignore){}
        runnable.run();
    }
}
