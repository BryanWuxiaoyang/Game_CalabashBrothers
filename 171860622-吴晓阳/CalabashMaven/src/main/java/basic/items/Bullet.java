package basic.items;

import basic.Effects.DamageEffect;
import basic.Effects.DecelerationEffect;
import basic.basics.BasicItem;
import basic.basics.ItemTypeId;
import basic.traits.*;
import game_console.GameConsole;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import utils.Utils;
import javafx.animation.*;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collection;

public class Bullet extends BasicItem implements Interactor, Emission, HasMove{
    public static final Image bulletImage = new Image("bullet.jpg", 20, 20, true, true);

    public static final Image coinImage = new Image("coin.jpg", 20, 20, true, true);

    public static final Image fireballImage = new Image("fireball.jpg", 40, 40, false, true);

    private double speed;

    private double damage;

    private double degree;

    private Dimension2D momentum;

    private double srcX;

    private double srcY;

    public Bullet(ItemTypeId itemTypeId, Image image, double speed, double damage, double degree, double srcX, double srcY){
        super(itemTypeId, image);
        this.speed = speed;
        this.damage = damage;
        this.degree = degree;
        this.momentum = Utils.generateDimension(speed, degree);
        this.srcX = srcX;
        this.srcY = srcY;
        getNode().setRotate(degree);
        //this.getNode().setLayoutX(srcX);
        //this.getNode().setLayoutY(srcY);
    }

    private boolean animationGenerated = false;

    public void generateAnimations() {
        super.getAnimations().clear();

        double time = 20;
        double dstX = srcX + momentum.getWidth() * 20;
        double dstY = srcY + momentum.getHeight() * 20;
        Path path = new Path();
        path.getElements().addAll(
                new MoveTo(srcX - getNode().getLayoutX(), srcY - getNode().getLayoutY()),
                new LineTo(dstX - getNode().getLayoutX(), dstY - getNode().getLayoutY()));
        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.seconds(time));
        transition.setNode(getNode());
        transition.setPath(path);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                setRemoved(true);
            }
        });

        super.getAnimations().add(transition);
    }

    @Override
    public Collection<Animation> getAnimations(){
        if(!animationGenerated) {
            generateAnimations();
            animationGenerated = true;
        }
        return super.getAnimations();
    }

    public void interactWith(Interacted item) {
        if(isLoading()) {this.setRemoved(true); return;}
        if(!isRemoved() && item != getEmitter()){
            if(item instanceof HasLife) {
                item.acceptInteract(new DamageEffect(this, (HasLife) item, damage));
            }
            if(item instanceof HasMove){
                //item.acceptInteract(new DecelerationEffect(this, (HasMove) item, 0.6, Duration.millis(3000)));
            }

            this.setRemoved(true);
        }
    }

    Emitter emitter = null;

    public Emitter getEmitter() {
        return emitter;
    }

    public void setEmitter(Emitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void setRemoved(boolean tag) {
        if(!isRemoved() && tag){
            for(Animation animation: getAnimations()){
                animation.stop();
            }
        }

        super.setRemoved(tag);
    }

    public Rectangle2D getInteractingRange() {
        return getCurrentBounds();
    }

    public double getSpeed() {
        return speed;
    }
}
