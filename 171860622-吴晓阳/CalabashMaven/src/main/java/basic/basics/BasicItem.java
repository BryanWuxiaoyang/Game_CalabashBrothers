package basic.basics;

import basic.traits.RemoveEvent;
import factory.TimeChecker;
import game_console.GameConsole;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class BasicItem implements Item {
    private Canvas canvas = new Canvas();

    private ItemTypeId itemTypeId;

    private static int idAlloc = 1;

    private int id;

    private GameConsole console;

    private Image image;

    TimeChecker timeChecker = new TimeChecker(TimeUnit.MILLISECONDS);

    private boolean loadingTag = false;

    public BasicItem(){
        this(ItemTypeId.BULLET, new Image("liuwa.jpg", 20, 20, true, true));
    }

    public BasicItem(ItemTypeId itemTypeId, Image image){
        super();
        this.itemTypeId = itemTypeId;
        this.image = image;
        this.id = idAlloc++;
        if(image != null) {
            canvas.setWidth(image.getWidth());
            canvas.setHeight(image.getHeight());
            GraphicsContext g = canvas.getGraphicsContext2D();
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
        }

        timeChecker.start();
    }

    public BasicItem(ItemTypeId itemTypeId, Image image, double width, double height){
        super();
        this.itemTypeId = itemTypeId;
        canvas.setWidth(width);
        canvas.setHeight(height);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.drawImage(image, 0, 0, width, height);
    }


    public boolean isLoading() {
        return loadingTag;
    }


    public void setLoading(boolean tag) {
        loadingTag = tag;
    }

    public Image getImage(){
        return image;
    }


    public void setConsole(GameConsole console) {
        this.console = console;
    }


    public GameConsole getConsole() {
        return console;
    }


    public ItemTypeId getItemTypeId() {
        return itemTypeId;
    }

    private boolean removeTag = false;

    private EventHandler<RemoveEvent> handler = null;


    public synchronized boolean isRemoved() {
        return removeTag;
    }


    public synchronized void setRemoved(boolean tag) {
        if(tag){
            if(!removeTag){
                removeTag = true;
                if(console != null) {
                    console.remove(this);
                }
                notifyOnRemove(new RemoveEvent(this, console));
                console = null;
            }
        }

    }


    public void setOnRemove(EventHandler<RemoveEvent> handler) {
        this.handler = handler;
    }

    private void notifyOnRemove(RemoveEvent event) {
        if(handler != null) handler.handle(event);
    }


    public Canvas getNode() {
        return canvas;
    }


    public int getItemId() {
        return id;
    }


    public Duration getLivingTime() {
        return Duration.millis(timeChecker.check());
    }

    private Collection<Animation> animations = Collections.synchronizedCollection(new ArrayList<Animation>());


    public Collection<Animation> getAnimations() {
        return animations;
    }

    private AtomicReference<Rectangle2D> savedBounds = new AtomicReference<Rectangle2D>(null);


    public Rectangle2D getSavedBounds() {
        return savedBounds.get();
    }


    public void setSavedBounds(Rectangle2D bounds) {
        this.savedBounds.set(bounds);
    }

    private AtomicReference<Rectangle2D> currentBounds = new AtomicReference<Rectangle2D>(null);


    public Rectangle2D getCurrentBounds() {
        Bounds bounds = getNode().getBoundsInParent();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        return new Rectangle2D(bounds.getMinX(), bounds.getMinY(), width >= 0 ? width : 0, height >= 0 ? height : 0);
        //return currentBounds.get();
    }


    public void saveCurrentBounds() {
        /*Bounds bounds = getNode().getBoundsInParent();
        this.currentBounds.set(new Rectangle2D(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));*/
    }
}
