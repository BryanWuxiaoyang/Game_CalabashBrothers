package game.towerdefence_game.effects;

import game.towerdefence_game.traits.Base;
import game.towerdefence_game.traits.Enemy;
import basic.Effects.Effect;

public class BaseAttackEffect extends Effect<Base, Enemy> {
    private double damage;

    public BaseAttackEffect(Base base, Enemy enemy, double damage){
        super(base, enemy);
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public void make() {
        getSource().decreaseLife(damage);
        getTarget().setRemoved(true);
    }
}
