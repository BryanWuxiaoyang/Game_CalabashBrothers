package basic.Creatures;

import basic.basics.BasicItem;
import basic.basics.ItemTypeId;
import basic.components.EmitComponent;
import basic.components.LifeComponent;
import basic.traits.*;
import factory.Creator;
import game_console.GameConsole;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class BasicCreature extends BasicItem implements HasLife, Emitter {
    public static final Image basicImage = new Image("liuwa.jpg", 50, 50, false, true);

    private EmitComponent emitComponent;

    private LifeComponent lifeComponent;

    public BasicCreature(){
        this(ItemTypeId.CREATURE, new Image("liuwa.jpg"));
    }

    public BasicCreature(ItemTypeId itemTypeId, Image image){
        this(itemTypeId, image, 500);
    }

    public BasicCreature(ItemTypeId itemTypeId, Image image, double maxLife){
        super(itemTypeId, image);
        this.emitComponent = new EmitComponent(this);
        this.lifeComponent = new LifeComponent(this, this.getNode().getGraphicsContext2D(), maxLife);
        this.lifeComponent.setOnDeath(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                BasicCreature.this.setRemoved(true);
            }
        });
    }

    @Override
    public void setConsole(GameConsole console) {
        super.setConsole(console);
        emitComponent.setConsole(console);
    }

    public void setEmissionCreator(Creator<? extends Emission> creator){
        emitComponent.setCreator(creator);
    }

    public void setEmissionCreator(Creator<? extends Emission> creator, Duration duration){
        emitComponent.setCreator(creator, duration);
    }


    public void emit() {
        emitComponent.emit();
    }


    public void setOnEmit(EventHandler<EmitEvent> handler) {
        emitComponent.setOnEmit(handler);
    }


    public void setEmitEnabled(boolean tag) {
        emitComponent.setEmitEnabled(tag);
    }


    public boolean isEmitEnabled() {
        return emitComponent.isEmitEnabled();
    }


    public double getMaxLife() {
        return lifeComponent.getMaxLife();
    }


    public double getLife() {
        return lifeComponent.getLife();
    }


    public void setMaxLife(double maxLife, boolean fillLife) {
        lifeComponent.setMaxLife(maxLife, fillLife);
    }


    public void increaseLife(double value) {
        lifeComponent.increaseLife(value);
    }


    public void decreaseLife(double value) {
        lifeComponent.decreaseLife(value);
    }


    public void setOnDeath(EventHandler<ActionEvent> handler) {
        lifeComponent.setOnDeath(handler);
    }


    public void setOnLifeChanged(EventHandler<LifeChangeEvent> handler) {
        lifeComponent.setOnLifeChanged(handler);
    }
}
