package game.towerdefence_game.creatures;

import basic.Creatures.BasicCreature;
import game.towerdefence_game.effects.BaseAttackEffect;
import game.towerdefence_game.traits.Enemy;
import basic.Effects.Effect;
import basic.basics.ItemTypeId;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class EnemyCreature extends BasicCreature implements Enemy {
    private double speed = 100;

    public static final Image scorpionImage = new Image("scorpion.jpg", 50, 50, true, true);

    public EnemyCreature(){
        super(ItemTypeId.CREATURE, scorpionImage, 200000);
    }

    @Override
    public void setRemoved(boolean tag) {
        super.setRemoved(tag);
    }

    @Override
    public double getSpeed(){
        return speed;
    }

    private final double damage = 10;

    @Override
    public void acceptInteract(Effect effect) {
        if(effect instanceof BaseAttackEffect){
            ((BaseAttackEffect) effect).setDamage(damage);
        }
        effect.make();
    }

    @Override
    public Rectangle2D getInteractedRange() {
        return getCurrentBounds();
    }
}
