package basic.components;

import basic.basics.Item;
import basic.basics.ItemTypeId;
import basic.traits.HasLife;
import basic.traits.LifeChangeEvent;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import utils.Utils;

import java.util.Collection;

public class LifeComponent{
    private Item item;

    private GraphicsContext g;

    public LifeComponent(Item item, GraphicsContext g){
        this(item, g, 100);
    }

    public LifeComponent(Item item, GraphicsContext g, double maxLife){
        this.item = item;
        this.g = g;
        this.maxLife = maxLife;
        this.life = maxLife;
        drawLife();
    }

    private void drawLife(){
        Rectangle2D bounds = item.getCurrentBounds();
        if(bounds != null) {
            double width = bounds.getWidth();
            double height = bounds.getHeight();
            Rectangle lifeRect = new Rectangle(0, 0, width, height / 5);
            Utils.drawLife(g, lifeRect, getLife(), getMaxLife());
        }
    }

    private double maxLife;

    private double life;

    private EventHandler<ActionEvent> deathHandler = null;

    private EventHandler<LifeChangeEvent> lifeChangeHandler = null;

    public double getMaxLife() {
        return maxLife;
    }

    public double getLife() {
        return life;
    }

    public void setMaxLife(double maxLife, boolean fillLife){
        this.maxLife = maxLife;
        if(maxLife < life) this.life = maxLife;
        else if(fillLife) this.life = maxLife;
    }

    public synchronized void increaseLife(double value) {
        double dstLife = life + value;
        if(dstLife > maxLife) dstLife = maxLife;
        else if(dstLife < 0) dstLife = 0;

        if(life == dstLife) return;

        double prevLife = life;
        life = dstLife;
        drawLife();

        notifyOnLifeChange(new LifeChangeEvent(item, prevLife, dstLife));
        if(dstLife == 0){
            notifyOnDeath(new ActionEvent(item, null));
            //item.setRemoved(true);
        }
    }

    public void decreaseLife(double value) {
       increaseLife(-value);
    }

    public void setOnLifeChanged(EventHandler<LifeChangeEvent> handler){
        this.lifeChangeHandler = handler;
    }

    private void notifyOnLifeChange(LifeChangeEvent event){
        if(lifeChangeHandler != null) lifeChangeHandler.handle(event);
    }

    public void setOnDeath(EventHandler<ActionEvent> handler) {
        this.deathHandler = handler;
    }

    private void notifyOnDeath(ActionEvent event){
        if(deathHandler != null) deathHandler.handle(new ActionEvent(this, null));
    }
}
