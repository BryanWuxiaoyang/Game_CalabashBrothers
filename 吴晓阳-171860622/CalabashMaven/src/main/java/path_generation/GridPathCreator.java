package path_generation;

import factory.Creator;
import maps.GridTexture;
import maps.grids.Grid;
import maps.GridType;

import java.util.*;

public class GridPathCreator implements Creator<List<Route>> {
    private Collection<Grid> srcGrids;

    private Collection<Grid> dstGrids;

    private List<Route> resultRouteSet = new ArrayList<Route>();

    private boolean finished = false;

    public GridPathCreator(Collection<Grid> srcGrids, Collection<Grid> dstGrids){
        this.srcGrids = srcGrids;
        this.dstGrids = dstGrids;
    }


    public List<Route> create() {
        if(finished) return resultRouteSet;
        else{
            for(Grid src : srcGrids){
                for(Grid dst : dstGrids){
                    Route route = new Route();
                    route.setSrcGrid(src);
                    route.setDstGrid(dst);
                    gridTags.clear();
                    boolean suc = findRoute(src, route);
                    if(suc) resultRouteSet.add(route);
                }
            }

            finished = true;
            return resultRouteSet;
        }
    }

    private Map<Grid, Integer> gridTags = new HashMap<Grid, Integer>();

    private boolean findRoute(Grid grid, Route resultRoute){
        boolean suc = false;
        boolean undetermined = false;
        gridTags.put(grid, 1);
        List<Grid> nextGrids = new ArrayList<Grid>();

        if(dstGrids.contains(grid)) suc = true;
        else{
            for(Grid nextGrid : grid.getAdjacentGrids()){
                if(!gridTags.containsKey(nextGrid)) gridTags.put(nextGrid, 0);
                int tag = gridTags.get(nextGrid);
                if(tag == 0) {
                    if(nextGrid.getType() == GridType.ROAD) {
                        boolean res = findRoute(nextGrid, resultRoute);
                        if (res) {
                            nextGrids.add(nextGrid);
                            suc = true;
                        }
                    }
                }
                else if(tag == 1){
                    undetermined = true;
                }
                else if(tag == 2){
                    nextGrids.add(nextGrid);
                    suc = true;
                }
                else if(tag == 3){}
                else throw new RuntimeException();
            }
        }

        if(suc) {
            resultRoute.addGrid(grid, nextGrids);
            gridTags.put(grid, 2);
        }
        else{
            if(undetermined) gridTags.put(grid, 0);
            else gridTags.put(grid, 3);
        }
        return suc;
    }
}
