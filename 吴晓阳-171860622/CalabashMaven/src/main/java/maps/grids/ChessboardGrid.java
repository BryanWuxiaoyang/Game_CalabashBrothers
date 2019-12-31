package maps.grids;

import maps.GridTexture;
import maps.GridType;

public class ChessboardGrid extends Grid{
    private int gridX;

    private int gridY;

    public ChessboardGrid(GridType type, GridTexture gridTexture, double x, double y, double width, double height, int gridX, int gridY) {
        super(type, gridTexture, x, y, width, height);
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public int getGridX(){
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
