package factory;

import java.util.concurrent.TimeUnit;

public class TimeChecker {
    private long start = 0;

    private long current = 0;

    private TimeUnit timeUnit;

    public TimeChecker(){
        this.timeUnit = TimeUnit.MILLISECONDS;
    }

    public TimeChecker(TimeUnit timeUnit){
        this.timeUnit = timeUnit;
    }

    public void start(){
        start = System.currentTimeMillis();
    }

    public long check(){
        current = System.currentTimeMillis();
        return timeUnit.convert(current - start, TimeUnit.MILLISECONDS);
    }
}
