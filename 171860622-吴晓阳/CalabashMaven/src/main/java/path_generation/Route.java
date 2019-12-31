package path_generation;

import maps.grids.Grid;
import utils.Utils;

import java.util.*;

public class Route {
    private Map<Grid, List<Grid>> nextGridSetMap = new HashMap<Grid, List<Grid>>();

    private Grid srcGrid;

    private Grid dstGrid;

    public void addGrid(Grid grid){
        if(!nextGridSetMap.containsKey(grid)) nextGridSetMap.put(grid, new ArrayList<Grid>());
    }

    public void addGrid(Grid grid, List<Grid> nextGrids){
        nextGridSetMap.put(grid, nextGrids);
    }

    public List<Grid> getNextGridSet(Grid grid){
        if(nextGridSetMap.containsKey(grid)) return nextGridSetMap.get(grid);
        else return null;
    }

    public boolean findGrid(Grid grid){
        return nextGridSetMap.containsKey(grid);
    }

    public Grid getSrcGrid() {
        return srcGrid;
    }

    public void setSrcGrid(Grid srcGrid){
        this.srcGrid = srcGrid;
    }

    public Grid getDstGrid() {
        return dstGrid;
    }

    public void setDstGrid(Grid dstGrid) {
        this.dstGrid = dstGrid;
    }

    public Iterator<Grid> iterator(){
        return new Iterator<Grid>() {
            private Grid grid = null;

            public boolean hasNext() {
                return grid != dstGrid;
            }

            public Grid next() {
                if(grid == null) grid = srcGrid;
                else {
                    List<Grid> list = getNextGridSet(grid);
                    grid = list.get(Utils.getRandom(list.size()));
                }
                return grid;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
