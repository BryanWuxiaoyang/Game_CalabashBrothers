package utils.workqueues;

import utils.Utils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BasicWorkQueue implements WorkQueue {
    private Queue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();

    private boolean runningTag = false;

    private Runnable platformRunnable = new Runnable() {
        public void run() {
            runningTag = true;
            int sleepTime = 0;

            Runnable runnable;
            while(true) {
                boolean hasRunnable = false;
                while ((runnable = queue.poll()) != null) {
                    hasRunnable = true;
                    runnable.run();
                }

                if(hasRunnable) sleepTime = 0;
                else{
                    if(sleepTime == 0) {
                        sleepTime = 10;
                        Thread.yield();
                    }
                    else {
                        sleepTime += sleepTime;
                        if (sleepTime > 2000) break;
                        else Utils.sleep(sleepTime);
                    }
                }
            }

            runningTag = false;
        }
    };

    public void runLater(Runnable runnable){
        queue.offer(runnable);
        if(!runningTag) {
            runningTag = true;
            Utils.getThreadPool().execute(platformRunnable);
        }
    }
}
