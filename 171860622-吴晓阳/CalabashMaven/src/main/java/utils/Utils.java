package utils;

import basic.basics.Item;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import maps.grids.Grid;
import optimizations.InteractionGridMap;
import org.omg.CORBA.portable.UnknownException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static Dimension2D generateDimension(double length, double degree){
        double rad = Math.toRadians(degree);
        return new Dimension2D(length * Math.cos(rad), length * Math.sin(rad));
    }

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService getThreadPool(){
        return executorService;
    }

    public static Timeline makeTaskAnimation(Duration duration, int cycleCount, Runnable... tasks){
        TaskKeyFrame frame = new TaskKeyFrame(duration, tasks);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(cycleCount);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(frame.getKeyFrame());
        return timeline;
    }

    public static void runTask(long interval, TimeUnit timeUnit, final Callable<Boolean> task){
        final long timeMillis = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
        getThreadPool().execute(new Runnable() {
            public void run() {
                try{
                    while(task.call()){
                        Thread.sleep(timeMillis);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void drawLife(final GraphicsContext g, final Rectangle lifeRect, final double life, final double maxLife){
        if(g == null) return;
        Platform.runLater(new Runnable() {
            public void run() {
                g.save();
                g.setFill(Color.GREEN);
                double greenLength = lifeRect.getWidth() * life / maxLife;
                g.fillRect(lifeRect.getX(), lifeRect.getY(), greenLength, lifeRect.getHeight());
                g.setFill(Color.RED);
                g.fillRect(lifeRect.getX() + greenLength, lifeRect.getY(), lifeRect.getWidth() - greenLength, lifeRect.getHeight());
                g.restore();
            }
        });
    }

    private static final Random rand = new Random(System.currentTimeMillis());

    public static int getRandom(int bound){
        return rand.nextInt(bound);
    }

    public static Timeline generateOnceTimeline(KeyFrame... frames){
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().addAll(frames);
        return timeline;
    }

    public static Point2D getCenter(Bounds bounds){
        return new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
    }

    public static Point2D getCenter(Rectangle bounds){
        return new Point2D(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
    }

    public static Point2D getCenter(Rectangle2D bounds){
        return new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
    }


    public static Point2D getSettingPos(Point2D center, double width, double height){
        return new Point2D(center.getX() - width / 2, center.getY() - height / 2);
    }

    public static Point2D getSettingPos(Bounds bounds, double width, double height){
        Point2D center = getCenter(bounds);
        Point2D pos = getSettingPos(center, width, height);
        return pos;
    }

    public static Point2D getSettingPos(Rectangle bounds, double width, double height){
        Point2D center = getCenter(bounds);
        Point2D pos = getSettingPos(center, width, height);
        return pos;
    }

    public static double calculateDistance(double srcX, double srcY, double dstX, double dstY){
        double width = dstX - srcX;
        double height = dstY - srcY;
        return Math.sqrt(width * width + height * height);
    }

    public static double calculateDegrees(double srcX, double srcY, double dstX, double dstY){
        double width = dstX - srcX;
        double height = dstY - srcY;
        double radian = Math.atan2(height, width);
        return Math.toDegrees(radian);
    }

    public static double calculateRadian(double srcX, double srcY, double dstX, double dstY){
        double width = dstX - srcX;
        double height = dstY - srcY;
        return Math.atan2(height, width);
    }

    public static void sleep(long timeMillis){
        try{Thread.sleep(timeMillis);}catch (InterruptedException ignore){}
    }

    private static AssignmentPool assignmentPool50ms = new AssignmentPool(Duration.millis(50));

    private static AssignmentPool assignmentPool200ms = new AssignmentPool(Duration.millis(200));

    private static AssignmentPool assignmentPool500ms = new AssignmentPool(Duration.millis(500));

    public static AssignmentPool getAssignmentPool50ms(){
        if(assignmentPool50ms.isOverLoaded()) {
            assignmentPool50ms = new AssignmentPool(Duration.millis(50));
        }
        return assignmentPool50ms;
    }

    public static AssignmentPool getAssignmentPool200ms(){
        if(assignmentPool200ms.isOverLoaded()){
            assignmentPool200ms = new AssignmentPool(Duration.millis(200));
        }
        return assignmentPool200ms;
    }

    public static AssignmentPool getAssignmentPool500ms(){
        if(assignmentPool500ms.isOverLoaded()){
            assignmentPool500ms = new AssignmentPool(Duration.millis(500));
        }
        return assignmentPool500ms;
    }

    private static Map<Integer, AssignmentPool> assignmentPoolMap = new HashMap<Integer, AssignmentPool>();

    public static AssignmentPool getAssignmentPool(int millis){
        if(millis == 50) return getAssignmentPool50ms();
        else if(millis == 200) return getAssignmentPool200ms();
        else if(millis == 500) return getAssignmentPool500ms();
        else{
            AssignmentPool assignmentPool = assignmentPoolMap.get(millis);
            if(assignmentPool == null || assignmentPool.isOverLoaded()){
                assignmentPool = new AssignmentPool(Duration.millis(millis));
                assignmentPoolMap.put(millis, assignmentPool);
            }

            return assignmentPool;
        }
    }

    public static Direction getDirection(int srcX, int srcY, int dstX, int dstY){
        int dx = dstX - srcX;
        int dy = dstY - srcY;
        if(dx > 0 && dy > 0) return Direction.DOWNRIGHT;
        else if(dx > 0 && dy == 0) return Direction.RIGHT;
        else if(dx > 0 && dy < 0) return Direction.UPRIGHT;
        else if(dx == 0 && dy > 0) return Direction.DOWN;
        else if(dx == 0 && dy == 0) return Direction.MIDDLE;
        else if(dx == 0 && dy < 0) return Direction.UP;
        else if(dx < 0 && dy > 0) return Direction.DOWNLEFT;
        else if(dx < 0 && dy == 0) return Direction.LEFT;
        else if(dx < 0 && dy < 0) return Direction.UPLEFT;
        else throw new RuntimeException();
    }
}
