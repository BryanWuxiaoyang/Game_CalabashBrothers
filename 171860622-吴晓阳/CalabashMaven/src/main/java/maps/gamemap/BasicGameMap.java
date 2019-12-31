package maps.gamemap;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import maps.GameBackground;
import maps.grids.Grid;
import maps.GridTexture;
import maps.GridType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicGameMap implements GameMap{
    private double width = 1000;

    private double height = 1000;

    private int gridRow = 20;

    private int gridCol = 20;

    private double gridWidth = width / gridCol;

    private double gridHeight = height / gridRow;

    private Group mapGroup = null;

    private List<Grid> enemySrc = null;

    private List<Grid> enemyDst = null;

    private Grid[][] map = null;

    private GameBackground background = GameBackground.BACKGROUND1;

    private boolean initTag = false;

    private void init(){
        map = new Grid[gridCol][gridRow];
        mapGroup = new Group();
        mapGroup.setLayoutX(0);
        mapGroup.setLayoutY(0);
        mapGroup.minWidth(width);
        mapGroup.minHeight(height);

        ImageView back = new ImageView(background.getImage(width, height));
        mapGroup.getChildren().add(back);

        int x, y;

        for(y = 0; y < gridRow; y++) {
            GridType type;
            GridTexture gridTexture;
            if(y == gridRow / 2 - 1 || y == gridRow / 2 + 1) {
                type = GridType.STAND;
                gridTexture = GridTexture.BLACK;
            }
            else if(y == gridRow / 2){
                type = GridType.ROAD;
                gridTexture = GridTexture.STONE;
            }
            else{
                continue;
            }

            for (x = 0; x < gridCol; x++) {
                Grid grid = new Grid(type, gridTexture, x * gridWidth, y * gridHeight, gridWidth, gridHeight);

                final int curX = x;
                final int curY = y;
                grid.getNode().setOnMousePressed(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        System.out.println("grid: (" + curX + ", " + curY + "), (" + event.getX() + ", " + event.getY() + ")");
                    }
                });

                mapGroup.getChildren().add(grid.getNode());
                map[x][y] = grid;
            }
        }

        for(x = 0; x < gridCol; x++){
            for(y = 0; y < gridRow; y++){
                if(map[x][y] != null){
                    if(x - 1 >= 0       && map[x - 1][y] != null) map[x][y].getAdjacentGrids().add(map[x - 1][y]);
                    if(x + 1 < gridCol  && map[x + 1][y] != null) map[x][y].getAdjacentGrids().add(map[x + 1][y]);
                    if(y - 1 >= 0       && map[x][y - 1] != null) map[x][y].getAdjacentGrids().add(map[x][y - 1]);
                    if(y + 1 < gridRow  && map[x][y + 1] != null) map[x][y].getAdjacentGrids().add(map[x][y + 1]);
                }
            }
        }

        enemySrc = new ArrayList<Grid>();
        enemySrc.add(map[gridCol - 1][gridRow / 2]);

        enemyDst = new ArrayList<Grid>();
        enemyDst.add(map[0][gridRow / 2]);

        initTag = true;
    }


    public List<Grid> getEnemySrc() {
        if(!initTag) init();
        return enemySrc;
    }


    public List<Grid> getEnemyDst() {
        if(!initTag) init();
        return enemyDst;
    }


    public Group getNode() {
        if(!initTag) init();
        return mapGroup;
    }


    public double getWidth() {
        if(!initTag) init();
        return width;
    }


    public double getHeight() {
        if(!initTag) init();
        return height;
    }


    public Grid getGrid(double x, double y) {
        if(!initTag) init();
        int xPos = (int)(x / gridWidth);
        int yPos = (int)(y / gridHeight);
        return map[xPos][yPos];
    }


    public Collection<Grid> getGrids() {
        if(!initTag) init();
        Collection<Grid> result = new ArrayList<Grid>();
        for(int x = 0; x < gridCol; x++){
            for(int y = 0; y < map[x].length; y++){
                if(map[x][y] != null){
                    result.add(map[x][y]);
                }
            }
        }
        return result;
    }


    public GameBackground getBackgound() {
        return null;
    }
}
