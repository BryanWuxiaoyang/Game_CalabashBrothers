package game.homework_game.effects;

import basic.Effects.DamageEffect;
import basic.basics.BasicItem;
import basic.basics.ItemTypeId;
import basic.traits.HasLife;
import basic.traits.Interacted;
import basic.traits.Interactor;
import factory.TimeChecker;
import game.homework_game.console.ChessboardGameConsole;
import game_console.GameConsole;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import maps.grids.Grid;
import utils.Utils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Catastrophe extends BasicItem implements Interactor {
    private static final Image image = new Image("fallingstone.jpg", 40, 40, false, true);

    private final Animation animation;

    private double speedRate = 1;

    private double speedPerSecond = 300;

    private double damage;

    private final double x;

    private final double y;

    private final double width;

    private final double height;

    private final Rectangle2D rangeRect;

    private Duration droppingTime;

    private Duration waitingTime = Duration.millis(200);

    public Catastrophe(GameConsole console, double x, double y, double width, double height){
        this(console, x, y, width, height, 30);
    }

    public Catastrophe(GameConsole console, double x, double y, double width, double height, double damage){
        super(ItemTypeId.BULLET, image);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rangeRect = new Rectangle2D(x, y, width, height);
        this.damage = damage;
        setConsole(console);
        this.animation = new PathTransition();

        Point2D center = Utils.getCenter(new Rectangle2D(x, y, width, height));
        Path path = new Path();
        double layoutX = getNode().getLayoutX();
        double layoutY = getNode().getLayoutY();
        path.getElements().addAll(
                new MoveTo(center.getX() - layoutX, 5 - layoutY),
                new LineTo(center.getX() - layoutX, center.getY() - layoutY)
        );
        ((PathTransition) animation).setNode(getNode());
        ((PathTransition) animation).setPath(path);

        droppingTime = Duration.seconds(center.getY() / speedPerSecond / speedRate);
        ((PathTransition) animation).setDuration(droppingTime);
        getAnimations().add(animation);
    }

    public void setDamage(double damage){
        this.damage = damage;
    }

    public double getDamage(){
        return damage;
    }

    private TimeChecker checker = new TimeChecker(TimeUnit.MILLISECONDS);

    private boolean checkerStarted = false;


    public void interactWith(Interacted item) {
        if(animation.getStatus() == Animation.Status.STOPPED){
            if(item instanceof HasLife) {
                item.acceptInteract(new DamageEffect(this, (HasLife) item, damage));
                setRemoved(true);
            }
        }
    }


    public Rectangle2D getInteractingRange() {
       if(animation.getStatus() == Animation.Status.STOPPED) {
           if(!checkerStarted) {
               checkerStarted = true;
               checker.start();
           }

           if(checker.check() > waitingTime.toMillis()) {
               setRemoved(true);
               return null;
           }
           else return rangeRect;
       }
       else return null;
    }
}
