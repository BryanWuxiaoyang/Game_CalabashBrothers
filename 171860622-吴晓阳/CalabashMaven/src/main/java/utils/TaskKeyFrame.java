package utils;

import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

class TaskKeyFrame{
    private KeyFrame frame;

    public TaskKeyFrame(Duration duration, final Runnable... runnables){
        double timeMillis = duration.toMillis();
        frame = new KeyFrame(Duration.millis(timeMillis), new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                for (Runnable runnable : runnables) {
                    runnable.run();
                }
            }
        });
    }

    public KeyFrame getKeyFrame(){
        return frame;
    }
}
