package game.homework_game.creatures;

import basic.basics.ItemTypeId;
import basic.items.Bullet;
import basic.traits.Interacted;
import factory.Creator;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;
import utils.Utils;

public class Calabash extends  BasicCreature implements ChessboardCreature, Interacted, Defender {

    private double damage = 3;

    private class CalabashBullet extends Bullet{
        public CalabashBullet(ItemTypeId itemTypeId, Image image, double speed, double damage, double degree, double srcX, double srcY) {
            super(itemTypeId, image, speed, damage, degree, srcX, srcY);
            this.setConsole(Calabash.this.getConsole());
        }

        @Override
        public void interactWith(Interacted item) {
            if(item instanceof Enemy) super.interactWith(item);
        }
    }

    private class CalabashBulletCreator implements Creator<Bullet> {

        public Bullet create() {
            Point2D center = Utils.getCenter(getCurrentBounds());
            Bullet bullet = new CalabashBullet(ItemTypeId.BULLET, Bullet.bulletImage, 700, damage, Calabash.this.getDegree(), center.getX(), center.getY());
            bullet.setLoading(Calabash.this.isLoading());
            return bullet;
        }
    }

    public Calabash(Image image){
        super(image);
        setEmissionCreator(new CalabashBulletCreator(), Duration.millis(300));
    }

    @Override
    public void setDamage(double damage){
        this.damage = damage;
    }

    @Override
    public double getDamage(){
        return damage;
    }
}
