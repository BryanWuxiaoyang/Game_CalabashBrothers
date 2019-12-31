package game.homework_game.creatures;

import basic.basics.ItemTypeId;
import basic.items.Bullet;
import basic.traits.Interacted;
import factory.Creator;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;
import utils.Utils;

public class Enemy extends BasicCreature implements ChessboardCreature, Interacted{

    private double damage = 10;

    private class EnemyBullet extends Bullet{
        public EnemyBullet(ItemTypeId itemTypeId, Image image, double speed, double damage, double degree, double srcX, double srcY) {
            super(itemTypeId, image, speed, damage, degree, srcX, srcY);
            this.setConsole(Enemy.this.getConsole());
        }

        @Override
        public void interactWith(Interacted item) {
            if(item instanceof Defender) {
                super.interactWith(item);
            }
        }
    }

    private class EnemyBulletCreator implements Creator<Bullet> {
        @Override
        public Bullet create() {
            Point2D center = Utils.getCenter(getCurrentBounds());
            Bullet bullet = new EnemyBullet(ItemTypeId.BULLET, Bullet.bulletImage, 700, damage, Enemy.this.getDegree(), center.getX(), center.getY());
            bullet.setLoading(Enemy.this.isLoading());
            return bullet;
        }
    }

    public Enemy(Image image){
        super(image);
        setEmissionCreator(new EnemyBulletCreator(), Duration.millis(300));
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
