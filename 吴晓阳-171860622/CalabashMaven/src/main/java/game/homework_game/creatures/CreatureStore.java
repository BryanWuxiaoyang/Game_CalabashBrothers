package game.homework_game.creatures;

import basic.basics.ItemTypeId;
import basic.items.Bullet;
import basic.traits.Emission;
import basic.traits.Emitter;
import basic.traits.Interacted;
import factory.BasicCreator;
import factory.Creator;
import factory.TimeChecker;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import utils.Utils;

import java.util.concurrent.TimeUnit;

public class CreatureStore {
    public static class Dawa extends Calabash {
        private static final Image image = new Image("dawa.jpg", 50, 50, true, true);

        private Dawa(){
            super(image);
            setMaxLife(150, true);
            setInteractingRadian(3);
            setDamage(getDamage() * 5);
        }
    }

    public static class Erwa extends Calabash{
        private static final Image image = new Image("erwa.jpg", 50, 50, true, true);

        private Erwa(){
            super(image);
            setMaxLife(50, true);
            setInteractingRadian(10);
        }
    }

    public static class Sanwa extends Calabash{
        private static final Image image = new Image("sanwa.jpg", 50, 50, true, true);

        private Sanwa(){
            super(image);
            setMaxLife(100, true);
        }
    }

    public static class Siwa extends Calabash{
        private static final Image image = new Image("siwa.jpg", 50, 50, true, true);

        private TimeChecker timeChecker = new TimeChecker();

        private Siwa(){
            super(image);
            setMaxLife(100, true);
            setInteractingRadian(8);
            setEmissionCreator(new Creator<Emission>() {
                private double degree = 0;

                public Emission create() {
                    Point2D center = Utils.getCenter(getCurrentBounds());
                    degree = (degree + 45) % 360;
                    return new Bullet(ItemTypeId.BULLET, Bullet.fireballImage, 200, getDamage(), degree, center.getX(), center.getY()){
                        @Override
                        public void interactWith(Interacted item) {
                            if(item instanceof Enemy) super.interactWith(item);
                        }
                    };
                }
            });
            timeChecker.start();
        }

        @Override
        public void emit(){
            if(timeChecker.check() >= 500){
                timeChecker.start();

                Platform.runLater(new Runnable() {
                    public void run() {
                        for(int i = 0; i < 8; i++){
                            Siwa.super.emit();
                        }
                    }
                });
            }
        }
    }

    public static class Wuwa extends Calabash{
        private static final Image image = new Image("wuwa.jpg", 50, 50, true, true);

        private Wuwa(){
            super(image);
            setMaxLife(100, true);
        }
    }

    public static class Liuwa extends Calabash{
        private static final Image image = new Image("liuwa.jpg", 50, 50, true, true);

        private Liuwa(){
            super(image);
            setSpeedRate(3);
            setMaxLife(80, true);
        }
    }

    public static class Qiwa extends Calabash{
        private static final Image image = new Image("qiwa.jpg", 50, 50, true, true);

        private Qiwa(){
            super(image);
            setMaxLife(100, true);
        }
    }

    public static class Yeye extends BasicCreature implements Defender{
        private static final Image image = new Image("yeye.jpg", 50, 50, true, true);

        private double damage = 3;

        private TimeChecker timeChecker = new TimeChecker(TimeUnit.MILLISECONDS);
        private Yeye(){
            super(image);
            setMaxLife(50, true);
            setEmissionCreator(new Creator<Emission>() {
                private double degree = 0;

                public Emission create() {
                    Point2D center = Utils.getCenter(getCurrentBounds());
                    degree = (degree + 22.5) % 360;
                    return new Bullet(ItemTypeId.BULLET, Bullet.coinImage, 300, damage, degree - 22.5, center.getX(), center.getY()){
                        {
                            setLoading(Yeye.this.isLoading());
                        }

                        @Override
                        public void interactWith(Interacted item) {
                            if(item instanceof Enemy) super.interactWith(item);
                            else if(item instanceof Calabash) {
                                if(!isLoading()) ((Calabash) item).increaseLife(damage * 2);
                                this.setRemoved(true);
                            }
                        }
                    };
                }
            });
            timeChecker.start();
        }

        @Override
        public void emit(){
            if(timeChecker.check() >= 500){
                timeChecker.start();

                Platform.runLater(new Runnable() {
                    public void run() {
                        for(int i = 0; i < 16; i++){
                            Yeye.super.emit();
                        }
                    }
                });
            }
        }

        @Override
        public void setTarget(ChessboardCreature target) {}

        @Override
        public void setDamage(double damage) {
            this.damage = damage;
        }

        @Override
        public double getDamage() {
            return damage;
        }
    }

    public static class Snake extends Enemy {
        private static final Image image = new Image("snake.jpg", 50, 50, true, true);

        private Snake(){
            super(image);
            setMaxLife(200, true);
        }
    }

    public static class Scorpion extends Enemy {
        private static final Image image = new Image("scorpion.jpg", 50, 50, true, true);

        private Scorpion(){
            super(image);
            setMaxLife(100, true);
        }
    }

    public static class Toad extends Enemy {
        private static final Image image = new Image("toad.jpg", 50, 50, true, true);

        private Toad(){
            super(image);
            setMaxLife(70, true);
        }
    }

    public static Calabash getCalabash(int index){
        switch (index){
            case 0: return new Dawa();
            case 1: return new Erwa();
            case 2: return new Sanwa();
            case 3: return new Siwa();
            case 4: return new Wuwa();
            case 5: return new Liuwa();
            case 6: return new Qiwa();
            default: throw new UnsupportedOperationException();
        }
    }

    public static BasicCreature getYeye(){
        return new Yeye();
    }

    public static Enemy getEnemy(int index){
        switch (index){
            case 0: return new Snake();
            case 1: return new Scorpion();
            default: return new Toad();
        }
    }
}
