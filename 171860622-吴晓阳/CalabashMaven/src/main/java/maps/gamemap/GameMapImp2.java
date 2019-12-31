package maps.gamemap;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import maps.GameBackground;
import maps.GridTexture;
import maps.GridType;
import maps.grids.ChessboardGrid;
import maps.grids.Grid;

import java.util.Collection;
import java.util.List;

public class GameMapImp2 implements ChessboardGameMap {
    private static final int[][] map = new int[20][];{
        map[0] = new int[]  {0, 2, 2, 0, 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 0, 0};
        map[1] = new int[]  {0, 2, 2, 0, 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 0, 0};
        map[2] = new int[]  {0, 2, 2, 0, 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 0, 0};
        map[3] = new int[]  {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 0, 0};
        map[4] = new int[]  {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0};
        map[5] = new int[]  {0, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0};
        map[6] = new int[]  {0, 2, 0, 0, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0};
        map[7] = new int[]  {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0};
        map[8] = new int[]  {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0};
        map[9] = new int[]  {2, 0, 0, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 2, 0, 0, 0, 0};
        map[10] = new int[] {2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0};
        map[11] = new int[] {2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 0, 0, 0, 0};
        map[12] = new int[] {2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0};
        map[13] = new int[] {2, 0, 0, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0};
        map[14] = new int[] {2, 0, 0, 2, 2, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        map[15] = new int[] {2, 0, 0, 2, 2, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        map[16] = new int[] {2, 0, 0, 2, 2, 2, 2, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        map[17] = new int[] {2, 0, 0, 2, 2, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        map[18] = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        map[19] = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static final int rowSize = 20;

    private static final int colSize = 20;

    private static final double gridWidth = 50;

    private static final double gridHeight = 50;

    private static final double width = colSize * gridWidth;

    private static final double height = rowSize * gridHeight;

    private final ChessboardGrid[][] grids = new ChessboardGrid[colSize][rowSize];

    private final GameBackground background = GameBackground.BACKGROUND2;

    private final Group group = new Group();

    private final Group backGroundGroup = new Group();

    private final Group gridGroup = new Group();

    public GameMapImp2(){
        group.getChildren().addAll(backGroundGroup, gridGroup);
        ImageView view = new ImageView(background.getImage(width, height));
        backGroundGroup.getChildren().add(view);

        int x, y;
        for(x = 0; x < colSize; x++){
            for(y = 0; y < rowSize; y++){
                switch(map[y][x]){
                    case 0: if(grids[x][y] == null) addGrid(GridType.BLOCK, null, x, y); break;
                    case 1: if(grids[x][y] == null) addGrid(GridType.STAND, null, x, y); break;
                    case 2: if(grids[x][y] == null) addGrid(GridType.ROAD, GridTexture.STONE, x, y); break;
                    default: throw new RuntimeException();
                }
            }
        }

        for(x = 0; x < colSize; x++){
            for(y = 0; y < rowSize; y++){
                if(grids[x][y] != null){
                    if(x - 1 >= 0       && grids[x - 1][y] != null) grids[x][y].getAdjacentGrids().add(grids[x - 1][y]);
                    if(x + 1 < colSize  && grids[x + 1][y] != null) grids[x][y].getAdjacentGrids().add(grids[x + 1][y]);
                    if(y - 1 >= 0       && grids[x][y - 1] != null) grids[x][y].getAdjacentGrids().add(grids[x][y - 1]);
                    if(y + 1 < rowSize  && grids[x][y + 1] != null) grids[x][y].getAdjacentGrids().add(grids[x][y + 1]);
                }
            }
        }
    }

    private void addGrid(GridType type, GridTexture texture, int x, int y){
        grids[x][y] = new ChessboardGrid(type, texture, x * gridWidth, y * gridHeight, gridWidth, gridHeight, x, y);
        gridGroup.getChildren().add(grids[x][y].getNode());
    }


    public int getRowSize() {
        return rowSize;
    }


    public int getColSize() {
        return colSize;
    }


    public double getGridWidth() {
        return gridWidth;
    }


    public double getGridHeight() {
        return gridHeight;
    }


    public ChessboardGrid getGridByGridPos(int x, int y) {
        return x >= 0 && x < colSize && y >= 0 && y < rowSize ? grids[x][y] : null;
    }


    public List<Grid> getEnemySrc() {
        return null;
    }


    public List<Grid> getEnemyDst() {
        return null;
    }


    public Group getNode() {
        return group;
    }


    public double getWidth() {
        return width;
    }


    public double getHeight() {
        return height;
    }


    public Grid getGrid(double x, double y) {
        return grids[(int)(x / gridWidth)][(int)(y / gridHeight)];
    }


    public Collection<Grid> getGrids() {
        throw new UnsupportedOperationException();
    }

    
    public GameBackground getBackgound() {
        return background;
    }
}
