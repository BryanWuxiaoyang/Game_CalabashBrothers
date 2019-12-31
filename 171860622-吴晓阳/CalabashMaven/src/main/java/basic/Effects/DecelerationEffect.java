package basic.Effects;

import basic.basics.Item;

import basic.traits.HasMove;
import javafx.animation.Animation;
import javafx.util.Duration;
import task.TimelineTask;

public class DecelerationEffect extends Effect<Item, HasMove> {
    private double rate;

    private Duration duration;

    public DecelerationEffect(Item source, HasMove target, double rate, Duration duration) {
        super(source, target);
        this.rate = rate;
        this.duration = duration;
    }

    @Override
    public void make() {
        for(Animation animation: target.getAnimations()){
            animation.setRate(animation.getRate() * rate);
        }
        new TimelineTask(new Runnable() {
            public void run() {
                for(Animation animation: target.getAnimations()){
                    animation.setRate(animation.getRate() / rate);
                }
            }
        }, duration, 1).run();
    }

    public double getRate() {
        return rate;
    }

    public Duration getDuration() {
        return duration;
    }
}
