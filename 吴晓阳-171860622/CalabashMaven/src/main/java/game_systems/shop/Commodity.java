package game_systems.shop;

import basic.basics.Item;
import factory.Creator;
import javafx.scene.Node;

public class Commodity{
    private final Creator<? extends Item> creator;

    private final String name;

    private final Node showNode;

    private final int value;

    private int num;

    public Commodity(Creator<? extends Item> creator, String name,  Node showNode, int value, int num){
        this.creator = creator;
        this.name = name;
        this.showNode = showNode;
        this.value = value;
        this.num = num;
    }

    public Creator<? extends Item> getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public Node getShowNode() {
        return showNode;
    }

    public int getValue() {
        return value;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
