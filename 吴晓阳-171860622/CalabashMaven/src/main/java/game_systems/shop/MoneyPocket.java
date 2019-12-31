package game_systems.shop;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MoneyPocket extends HBox {
    private Label promptLabel = new Label("金钱: ");

    private Label moneyLabel = new Label();

    private SimpleIntegerProperty money = new SimpleIntegerProperty(0);

    public MoneyPocket(){
        this(0);
    }

    public MoneyPocket(int money){
        super(10);
        this.money.set(money);
        this.getChildren().addAll(promptLabel, moneyLabel);
        this.money.addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                moneyLabel.setText(MoneyPocket.this.money.getValue().toString());
            }
        });

        moneyLabel.setText(Integer.toString(money));
    }

    public int getMoney() {
        return money.get();
    }

    public void setMoney(int money) {
        this.money.set(money);
    }

    public SimpleIntegerProperty moneyProperty(){
        return money;
    }

    public void increaseMoney(int value){
        money.set(money.get() + value);
    }

    public void decreaseMoney(int value){
        money.set(money.get() - value);
    }
}
