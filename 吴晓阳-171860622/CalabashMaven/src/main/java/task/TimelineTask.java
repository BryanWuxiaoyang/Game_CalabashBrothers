package task;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.concurrent.Callable;

public class TimelineTask implements Task {
    private Duration duration;

    private int cycleCount;

    private int doneCycleCount;

    private Timeline taskTimeline;

    private boolean waitTermination = false;

    private boolean runningTag = false;

    public TimelineTask(Runnable runnable) {
        this(runnable, Duration.ZERO, 1);
    }

    public TimelineTask(final Runnable runnable, Duration duration, int cycleCount){
        this.duration = duration;
        this.cycleCount = cycleCount;
        this.doneCycleCount = 0;
        this.taskTimeline = new Timeline();

        KeyFrame frame = new KeyFrame(duration, new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                runnable.run();
                doneCycleCount++;
            }
        });
        taskTimeline.getKeyFrames().add(frame);
        taskTimeline.setCycleCount(cycleCount);
    }

    public Duration getDuration() {
        return duration;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public int getDoneCycleCount() {
        return doneCycleCount;
    }


    public void run() {
        if(runningTag) return;

        runningTag = true;
        Platform.runLater(new Runnable() {
            public void run() {
                taskTimeline.play();
            }
        });
        if(waitTermination){
            try {
                Thread.sleep((int) (duration.toMillis() * cycleCount) + 100);
                while(taskTimeline.getStatus() != Animation.Status.STOPPED){
                    Thread.sleep((int)(duration.toMillis() / 5));
                }
            } catch (Exception e){
                throw new RuntimeException();
            }
            runningTag = false;
        }
    }


    public void pause() {
        Platform.runLater(new Runnable() {
            public void run() {
                taskTimeline.pause();
            }
        });
        runningTag = false;
    }


    public void stop() {
        Platform.runLater(new Runnable() {
            public void run() {
                taskTimeline.stop();
            }
        });
        runningTag = false;
    }


    public boolean isDone() {
        return doneCycleCount >= cycleCount;
    }


    public void setWaitTermination(boolean tag) {
        waitTermination = tag;
    }


    public boolean isWaitTermination() {
        return waitTermination;
    }
}
