package game_systems.shop;

import javafx.event.ActionEvent;
import javafx.event.Event;

public class PurchaseEvent extends ActionEvent {
    private Shop shop;

    private Commodity commodity;

    public PurchaseEvent(Shop shop, Commodity commodity){
        super(shop, null);
        this.shop = shop;
        this.commodity = commodity;
    }

    public Shop getShop() {
        return shop;
    }

    public Commodity getCommodity() {
        return commodity;
    }
}
