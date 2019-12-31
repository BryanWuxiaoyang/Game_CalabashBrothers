package optimizations;

import basic.basics.Item;
import basic.traits.Interacted;
import basic.traits.Interactor;
import javafx.geometry.Rectangle2D;
import utils.MyConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

public class InteractionGridMap {

    public static class InteractionSet{
        private Collection<Interactor> interactors = new ArrayList<Interactor>();

        private Collection<Interacted> interacteds = new ArrayList<Interacted>();

        public Collection<Interactor> getInteractors(){
            return interactors;
        }

        public Collection<Interacted> getInteracteds(){
            return interacteds;
        }
    }

    private final int width;

    private final int height;

    private final int gridWidth;

    private final int gridHeight;

    private final int gridRow;

    private final int gridCol;

    private final InteractionSet map[][];

    private final InteractionSet outerSet;

    public InteractionGridMap(int width, int height, int gridWidth, int gridHeight) {
        this.width = width;
        this.height = height;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gridCol = width / gridWidth;
        this.gridRow = height / gridHeight;
        this.map = new InteractionSet[this.gridCol][this.gridRow];
        this.outerSet = new InteractionSet();
    }

    private int getX(int x){
        return x / gridWidth;
    }

    private int getY(int y){
        return y / gridHeight;
    }

    private boolean checkInRange(int x, int y){
        return (x >= 0) && (getX(x) < gridCol) && (y > 0) && (getY(y) < gridRow);
    }

    private InteractionSet getSet(int x, int y){
        int xPos = getX(x);
        int yPos = getY(y);
        if(xPos >= 0 && xPos < gridCol && yPos >= 0 && yPos < gridRow){
            if(map[xPos][yPos] == null) map[xPos][yPos] = new InteractionSet();
            return map[xPos][yPos];
        }
        else return outerSet;
    }

    private InteractionSet getSet(Rectangle2D bounds){
        return bounds != null ? getSet((int)bounds.getMinX(), (int)bounds.getMinY()) : null;
    }

    private InteractionSet getSet(Item item){
        return getSet(item.getSavedBounds());
    }

    private InteractionSet getSetPermitNull(int x, int y){
        int xPos = getX(x);
        int yPos = getY(y);
        return (xPos >= 0 && xPos < gridCol && yPos >= 0 && yPos < gridRow) ? map[xPos][yPos] : outerSet;
    }

    private InteractionSet getSetPermitNull(Rectangle2D bounds){
        return bounds != null ? getSetPermitNull((int)bounds.getMinX(), (int)bounds.getMinY()) : null;
    }

    private InteractionSet getSetPermitNull(Item item){
        return getSetPermitNull(item.getSavedBounds());
    }

    private void add(Interactor item, InteractionSet set){
        if(set != null) set.getInteractors().add(item);
    }

    private void add(Interacted item, InteractionSet set){
        if(set != null) set.getInteracteds().add(item);
    }

    private void remove(Interactor item, InteractionSet set){
        if(set != null) set.getInteractors().remove(item);
    }

    private void remove(Interacted item, InteractionSet set){
        if(set != null) set.getInteracteds().remove(item);
    }

    public void add(Interactor item){
        add(item, getSet(item));
    }

    public void add(Interacted item){
        add(item, getSet(item));
    }

    public void remove(Interactor item){
        remove(item, getSet(item));
    }

    public void remove(Interacted item){
        remove(item, getSet(item));
    }

    private static class RefreshInteractor{
        private Interactor interactor;
        private InteractionSet srcSet;
        private InteractionSet dstSet;
        private Rectangle2D dstBounds;

        private RefreshInteractor(Interactor interactor, InteractionSet srcSet, InteractionSet dstSet, Rectangle2D dstBounds) {
            this.interactor = interactor;
            this.srcSet = srcSet;
            this.dstSet = dstSet;
            this.dstBounds = dstBounds;
        }
    }

    private static class RefreshInteracted{
        private Interacted interacted;
        private InteractionSet srcSet;
        private InteractionSet dstSet;
        private Rectangle2D dstBounds;

        private RefreshInteracted(Interacted interacted, InteractionSet srcSet, InteractionSet dstSet, Rectangle2D dstBounds) {
            this.interacted = interacted;
            this.srcSet = srcSet;
            this.dstSet = dstSet;
            this.dstBounds = dstBounds;
        }
    }

    private void addToRefreshList(Interactor item, Collection<RefreshInteractor> list){
        Rectangle2D savedBounds = item.getSavedBounds();
        Rectangle2D bounds = item.getCurrentBounds();
        if(bounds != null) {
            InteractionSet srcSet = getSet(savedBounds);
            InteractionSet dstSet = getSet(bounds);
            if (srcSet != dstSet) {
                list.add(new RefreshInteractor(item, srcSet, dstSet, bounds));
            }
        }
    }

    private void addToRefreshList(Interacted item, Collection<RefreshInteracted> list){
        Rectangle2D savedBounds = item.getSavedBounds();
        Rectangle2D bounds = item.getCurrentBounds();
        if(bounds != null) {
            InteractionSet srcSet = getSet(savedBounds);
            InteractionSet dstSet = getSet(bounds);
            if (srcSet != dstSet) {
                list.add(new RefreshInteracted(item, srcSet, dstSet, bounds));
            }
        }
    }

    public void refresh(){
        Collection<RefreshInteractor> interactors = new ArrayList<RefreshInteractor>();
        Collection<RefreshInteracted> interacteds = new ArrayList<RefreshInteracted>();

        for(int X = 0; X < gridCol; X++){
            for(int Y = 0; Y < gridRow; Y++){
                if(map[X][Y] != null) {
                    for(Interactor interactor: map[X][Y].getInteractors()){
                        addToRefreshList(interactor, interactors);
                    }
                    for(Interacted interacted: map[X][Y].getInteracteds()){
                        addToRefreshList(interacted, interacteds);
                    }
                }
            }
        }
        for(Interactor interactor: outerSet.getInteractors()){
            addToRefreshList(interactor, interactors);
        }
        for(Interacted interacted: outerSet.getInteracteds()){
            addToRefreshList(interacted, interacteds);
        }

        for(RefreshInteractor interactor: interactors){
            remove(interactor.interactor, interactor.srcSet);
            add(interactor.interactor, interactor.dstSet);
            interactor.interactor.setSavedBounds(interactor.dstBounds);
        }
        for(RefreshInteracted interacted: interacteds){
            remove(interacted.interacted, interacted.srcSet);
            add(interacted.interacted, interacted.dstSet);
            interacted.interacted.setSavedBounds(interacted.dstBounds);
        }
    }

    public void forEach(int x, int y, int width, int height, MyConsumer<InteractionSet> action){
        InteractionSet set;
        int X = Math.max(getX(x), 0);
        int Y = Math.max(getY(y), 0);
        int dstX = Math.min(getX(x + width) + 1, gridCol);
        int dstY = Math.min(getY(y + height) + 1, gridRow);
        for(int curX = X; curX < dstX; curX++){
            for(int curY = Y; curY < dstY; curY++){
                set = map[curX][curY];
                if(set != null) action.accept(set);
            }
        }
        if(!checkInRange(x + width, y + height) || !checkInRange(x, y)) action.accept(outerSet);
    }

    public void forEach(MyConsumer<InteractionSet> action){
        for(int X = 0; X < gridCol; X++){
            for(int Y = 0; Y < gridRow; Y++){
                if(map[X][Y] != null) action.accept(map[X][Y]);
            }
        }
        action.accept(outerSet);
    }
}
