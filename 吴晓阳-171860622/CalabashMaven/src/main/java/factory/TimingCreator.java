package factory;

import java.util.concurrent.TimeUnit;

public class TimingCreator<T> implements Creator<T>{
    private Creator<T> creator;

    private long interval;

    private TimeUnit timeUnit;

    private TimeChecker timeChecker;

    public TimingCreator(Creator<T> creator, long interval, TimeUnit timeUnit){
        this.creator = creator;
        this.interval = interval;
        this.timeUnit = timeUnit;
        this.timeChecker = new TimeChecker(timeUnit);

        timeChecker.start();
    }


    public T create() {
        long cur = timeChecker.check();
        T res;
        if(cur >= interval){
            res = creator.create();
        }
        else{
            try {
                Thread.sleep(timeUnit.toMillis(interval - cur));
            }catch (Exception e){
            }
            res = creator.create();
        }
        timeChecker.start();
        return res;
    }
}
