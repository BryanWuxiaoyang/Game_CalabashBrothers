package maps.gamemap;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import maps.GameBackground;
import maps.grids.ChessboardGrid;
import maps.grids.Grid;
import maps.GridTexture;
import maps.GridType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameMapImp1 implements ChessboardGameMap {
    private final double width = 1000;

    private final double height = 1000;

    private final int gridRow = 20;

    private final int gridCol = 20;

    private final double gridWidth = width / gridCol;

    private final double gridHeight = height / gridRow;

    private Group mapGroup = null;

    private Group backgoundGroup = null;

    private Group gridGroup = null;

    private List<Grid> enemySrc = null;

    private List<Grid> enemyDst = null;

    private ChessboardGrid[][] map = null;

    private GameBackground background = GameBackground.BACKGROUND1;

    private boolean initTag = false;

    private void addGrid(GridType type, GridTexture texture, int x, int y){
        ChessboardGrid grid = new ChessboardGrid(type, texture, x * gridWidth, y * gridHeight, gridWidth, gridHeight, x, y);
        map[x][y] = grid;
        gridGroup.getChildren().add(grid.getNode());
    }

    private void init(){
        mapGroup = new Group();
        backgoundGroup = new Group();
        gridGroup = new Group();
        mapGroup.getChildren().addAll(backgoundGroup, gridGroup);

        ImageView back = new ImageView(background.getImage(width * 2, height * 2));
        backgoundGroup.getChildren().add(back);

        map = new ChessboardGrid[gridCol][gridRow];
        GridType type = null;
        GridTexture texture = null;
        int x, y;

        type = GridType.ROAD;
        texture = GridTexture.STONE;
        y = 1;
        for(x = 0; x < gridCol - 2; x++){
            if(map[x][y] == null) addGrid(type, texture, x, y);
        }

        x = gridCol - 3;
        for(y = 1; y < gridRow / 2; y++){
            if(map[x][y] == null) addGrid(type, texture, x, y);
        }

        x = 2;
        for(y = 1; y < gridRow / 2; y++){
            if(map[x][y] == null) addGrid(type, texture, x, y);
        }

        y = gridRow / 2 - 1;
        for(x = gridCol - 3; x >= 2; x--){
            if(map[x][y] == null) addGrid(type, texture, x, y);
        }

        x = 2;
        for(y = gridRow / 2 - 1; y < gridRow * 3 / 4; y++){
            if(map[x][y] == null) addGrid(type, texture, x, y);
        }

        y = gridRow * 3 / 4 - 1;
        for(x = 2; x < gridCol / 2; x++){
            if(map[x][y] == null) addGrid(type, texture, x, y);
        }

        x = gridCol / 2 - 1;
        for(y = gridRow * 3 / 4 - 1; y < gridRow; y++){
            if(map[x][y] == null) addGrid(type, texture, x, y);
        }

        type = GridType.STAND;
        texture = null;
        for(x = 0; x < gridCol; x++){
            for(y = 0; y < gridRow; y++){
                if(map[x][y] == null) addGrid(type, texture, x, y);
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
        enemyDst = new ArrayList<Grid>();

        enemySrc.add(map[gridCol / 2 - 1][gridRow - 1]);
        enemyDst.add(map[0][1]);

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


    public ChessboardGrid getGrid(double x, double y) {
        if(!initTag) init();

        if(x / gridWidth < 0 || x / gridWidth >= gridCol || y / gridHeight < 0 || y / gridHeight >= gridRow) return null;
        else return map[(int)(x / gridWidth)][(int)(y / gridHeight)];
    }


    public Collection<Grid> getGrids() {
        if(!initTag) init();
        Collection<Grid> grids = new ArrayList<Grid>();
        for(int x = 0; x < gridCol; x++){
            for(int y = 0; y < gridRow; y++){
                if(map[x][y] != null) grids.add(map[x][y]);
            }
        }
        return grids;
    }


    public GameBackground getBackgound() {
        if(!initTag) init();
        return background;
    }


    public int getRowSize() {
        return gridRow;
    }


    public int getColSize() {
        return gridCol;
    }


    public double getGridWidth() {
        return gridWidth;
    }


    public double getGridHeight() {
        return gridHeight;
    }


    public ChessboardGrid getGridByGridPos(int x, int y) {
        return map[x][y];
    }
}
