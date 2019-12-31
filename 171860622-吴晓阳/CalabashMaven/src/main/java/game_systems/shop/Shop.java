package game_systems.shop;

import basic.basics.Item;
import factory.Creator;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.*;

public class Shop extends BorderPane {

    private List<Page> pages = new ArrayList<Page>();

    private PageCreator pageCreator = new PageCreator();

    private int curPageIndex;

    private Commodity curCommodity;

    private MoneyPocket moneyPocket;

    private Label shopLabel = new Label("商城");

    private Label pageLabel = new Label();

    private HBox headBox = new HBox(shopLabel, pageLabel);

    private BorderPane showWindow = new BorderPane();

    private Button prevPageButton = new Button("上一页");
    {
        prevPageButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (curPageIndex - 1 >= 0) toPrevPage();
            }
        });
    }

    private Button nextPageButton = new Button("下一页");
    {
        nextPageButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (curPageIndex + 1 < pages.size()) toNextPage();
            }
        });
    }

    private Button purchaseButton = new Button("购买");
    {
        purchaseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                purchase(moneyPocket, curCommodity);
            }
        });
    }

    private HBox buttonBox = new HBox(prevPageButton, nextPageButton, purchaseButton);

    private BorderPane moneyPocketWindow = new BorderPane();

    private Label infoLabel = new Label("请选择购买商品");

    private VBox mainWindow = new VBox(showWindow, buttonBox, moneyPocketWindow, infoLabel);

    {
        this.setTop(headBox);
        this.setCenter(mainWindow);
    }


    private final int row;

    private final int col;

    public Shop(){
        this("商店");
    }

    public Shop(String shopName){
        this(shopName, 2, 4);
    }

    public Shop(String shopName, int row, int col){
        this.shopLabel.setText(shopName);

        this.row = row;
        this.col = col;
        this.curPageIndex = -1;
        this.curCommodity = null;
        this.moneyPocket = null;

        addPage();
        toNextPage();
    }

    private void toPrevPage(){
        curPageIndex--;
        showWindow.setCenter(pages.get(curPageIndex).getPane());
        curCommodity = null;
        pageLabel.setText("当前页面： " + (curPageIndex + 1) + "/" + pages.size());
    }

    private void toNextPage(){
        curPageIndex++;
        showWindow.setCenter(pages.get(curPageIndex).getPane());
        curCommodity = null;
        pageLabel.setText("当前页面： " + (curPageIndex + 1) + "/" + pages.size());
    }

    private void addPage(){
        pages.add(pageCreator.create());
        pageLabel.setText("当前页面： " + (curPageIndex + 1) + "/" + pages.size());
    }

    private Page getTailPage(){
        return pages.get(pages.size() - 1);
    }

    public void setMoneyPocket(MoneyPocket moneyPocket) {
        this.moneyPocketWindow.setCenter(moneyPocket);
        this.moneyPocket = moneyPocket;
    }

    public MoneyPocket getMoneyPocket(){
        return this.moneyPocket;
    }

    public void add(Creator<? extends Item> creator, String name, Node showNode, int value, int num){
        add(new Commodity(creator, name, showNode, value, num));
    }

    public void add(final Commodity commodity){
        if(getTailPage().getCommodities().size() >= row * col) {
            addPage();
        }

        commodity.getShowNode().setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                infoLabel.setText("当前选择： " + commodity.getName());
                curCommodity = commodity;
            }
        });

        getTailPage().add(commodity);
    }

    public Commodity getCurCommodity(){
        return curCommodity;
    }

    public Item purchase(){
        return purchase(getMoneyPocket(), getCurCommodity());
    }

    public Item purchase(MoneyPocket moneyPocket, Commodity commodity){
        Item result = null;
        if(curCommodity != null){
            if(moneyPocket != null && moneyPocket.getMoney() >= commodity.getValue()){
                result = commodity.getCreator().create();
                moneyPocket.decreaseMoney(commodity.getValue());
            }
        }
        if(result != null) infoLabel.setText("购买成功： " + commodity.getName());
        else infoLabel.setText("购买失败");
        return result;
    }

    private EventHandler<PurchaseEvent> purchaseEventHandler = null;

    public void setOnPurchase(EventHandler<PurchaseEvent> handler){
        purchaseEventHandler = handler;
    }

    public EventHandler<PurchaseEvent> getOnPurchase(){
        return purchaseEventHandler;
    }

    class Page{
        private TilePane pane;
        private Collection<Commodity> commodities;

        Page(TilePane pane, Collection<Commodity> commodities) {
            this.pane = pane;
            this.commodities = commodities;
        }

        TilePane getPane() {
            return pane;
        }

        Collection<Commodity> getCommodities() {
            return commodities;
        }

        public void add(Commodity commodity){
            pane.getChildren().add(commodity.getShowNode());
            commodities.add(commodity);
        }
    }

    class PageCreator implements Creator<Page>{
        public Page create() {
            TilePane pane = new TilePane(Orientation.HORIZONTAL, 10, 10);
            pane.setPrefRows(row);
            pane.setPrefColumns(col);

            Collection<Commodity> commodities = new ArrayList<Commodity>();

            return new Page(pane, commodities);
        }
    }
}
