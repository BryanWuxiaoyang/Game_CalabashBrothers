package game.towerdefence_game.creatures;

import basic.Creatures.BasicCreature;
import game.towerdefence_game.traits.Defender;
import game.towerdefence_game.traits.Enemy;
import basic.basics.ItemTypeId;
import factory.Creator;
import basic.items.Bullet;
import basic.traits.Interacted;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;
import task.AssignmentPoolTask;
import utils.Utils;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.concurrent.Callable;

public class Archer extends BasicCreature implements Defender {

    public Archer(){
        this(BasicCreature.basicImage);
    }

    public Archer(Image image){
        super(ItemTypeId.CREATURE, image);
        //this.setEmissionCreator(new TimingCreator<>(new ArcherCreator(), 200, TimeUnit.MILLISECONDS));
        this.setEmissionCreator(new ArcherCreator(), Duration.millis(200));
        this.setEmitEnabled(false);
        new AssignmentPoolTask(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                if (isRemoved()) return false;

                if (hasInteraction) {
                    hasInteraction = false;
                    setEmitEnabled(true);
                } else {
                    setEmitEnabled(false);
                }

                return true;
            }
        }, Duration.millis(1000), -1).run();
    }

    private double nextDegree = 0;

    private double nextX = 0;

    private double nextY = 0;

    private double generateDegree(double dstX, double dstY){
        Rectangle2D bounds = getCurrentBounds();
        double width = dstX - (bounds.getMinX() + bounds.getWidth() / 2);
        double height = dstY - (bounds.getMinY() + bounds.getHeight() / 2);
        double rad = Math.atan2(height, width);
        double degree = Math.toDegrees(rad);
        return degree;
    }

    private boolean hasInteraction = false;


    public void interactWith(Interacted item) {
        if(!item.isRemoved() && item instanceof Enemy){
            Rectangle2D bounds = item.getCurrentBounds();
            if(bounds != null) {
                nextX = bounds.getMinX() + bounds.getWidth() / 2;
                nextY = bounds.getMinY() + bounds.getHeight() / 2;
                nextDegree = generateDegree(nextX, nextY);
                hasInteraction = true;
            }
        }
    }


    public Rectangle2D getInteractingRange() {
        Rectangle2D bounds = getCurrentBounds();
        if(bounds != null) {
            Point2D center = Utils.getCenter(bounds);
            return new Rectangle2D(center.getX() - 300, center.getY() - 300, 600, 600);
        }
        else{
            return null;
        }
    }

    public class ArcherCreator implements Creator<ArcherBullet> {

        public ArcherBullet create() {
            return new ArcherBullet();
        }
    }

    public class ArcherBullet extends Bullet{
        private static final double bulletSpeed = 300;

        ArcherBullet(){
            super(ItemTypeId.BULLET,
                    Bullet.bulletImage,
                    bulletSpeed,
                    20,
                    nextDegree,
                    Archer.this.getCurrentBounds().getMinX() + Archer.this.getCurrentBounds().getWidth() / 2,
                    Archer.this.getCurrentBounds().getMinY() + Archer.this.getCurrentBounds().getHeight() / 2);
        }

        @Override
        public void interactWith(Interacted item) {
            if(item instanceof Enemy) {
                super.interactWith(item);
            }
        }
    }
}
