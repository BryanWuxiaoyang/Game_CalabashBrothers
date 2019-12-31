package maps.grids;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import maps.GameBackground;
import maps.GridTexture;
import maps.GridType;

import java.util.ArrayList;
import java.util.Collection;

public class Grid{
    private Canvas canvas;

    private GridType type;

    private GridTexture gridTexture;

    private Collection<Grid> adjacentGrids = new ArrayList<Grid>();

    public Grid(GridType type, GridTexture gridTexture, double x, double y, double width, double height) {
        super();
        this.type = type;
        this.gridTexture = gridTexture;
        this.canvas = new Canvas();
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        canvas.setWidth(width);
        canvas.setHeight(height);
        //this.setDepth(type == GridType.ROAD? 0 : 20);

        GraphicsContext g = canvas.getGraphicsContext2D();
        if(gridTexture != null){
            g.drawImage(gridTexture.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight());
        }
        else{
            g.save();
            g.setFill(Color.BLACK);
            g.fill();
            g.restore();
        }
    }

    public GridType getType() {
        return type;
    }

    public GridTexture getTexture() {
        return gridTexture;
    }

    public Collection<Grid> getAdjacentGrids(){
        return adjacentGrids;
    }

    private int pathFinderTag = 0;

    public int getPathFinderTag() {
        return pathFinderTag;
    }

    public void setPathFinderTag(int pathFinderTag) {
        this.pathFinderTag = pathFinderTag;
    }

    public Node getNode(){
        return canvas;
    }
}
