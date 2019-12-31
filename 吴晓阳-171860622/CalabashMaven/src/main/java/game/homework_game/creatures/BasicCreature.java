package game.homework_game.creatures;

import basic.Effects.Effect;
import basic.basics.ItemTypeId;
import basic.items.Bullet;
import basic.traits.Interacted;
import basic.traits.RemoveEvent;
import game.homework_game.components.ChessboardCreatureComponent;
import game.homework_game.console.ChessboardGameConsole;
import game_console.GameConsole;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.util.Duration;
import maps.GridType;
import maps.grids.Grid;
import task.AssignmentPoolTask;
import utils.Direction;
import utils.Utils;

import java.util.*;
import java.util.concurrent.Callable;

public abstract class BasicCreature extends basic.Creatures.BasicCreature implements ChessboardCreature, Interacted {

    private double speedRate = 1;

    private double degree = 0;

    private ChessboardCreature target = null;

    private Animation moveAnimation = null;

    private ChessboardCreatureComponent chessboardCreatureComponent = new ChessboardCreatureComponent();

    private double interactingRadian = 3;

    private EventHandler<MoveEvent> moveHandler = null;

    private void notifyMove(MoveEvent event){
        if(moveHandler != null) moveHandler.handle(event);
    }

    public void setOnMove(EventHandler<MoveEvent> handler){
        this.moveHandler = handler;
    }

    public synchronized boolean moveTo(final int x, final int y, boolean requireEmpty) {
        if(moveAnimation != null && moveAnimation.getStatus() != Animation.Status.STOPPED) return false;
        boolean suc = false;
        ChessboardGameConsole console = getConsole();

        if(console != null) {
            console.getLock().lock();
            Grid grid = console.getGameMap().getGridByGridPos(x, y);
            if (grid != null) {
                if(requireEmpty) suc = (grid.getType() == GridType.ROAD) && console.moveOnChessboardRequireEmpty(this, x, y);
                else suc = console.moveOnChessboard(this, x, y);

                if(suc) {
                    notifyMove(new MoveEvent(this, x, y));
                    Platform.runLater(new Runnable() {
                        public void run() {
                            makeMoveAnimation(x, y);
                        }
                    });
                }
            }
            console.getLock().unlock();
        }
        return suc;
    }

    public synchronized boolean moveUp(){
        return moveUp(true);
    }

    public synchronized boolean moveUp(boolean requireEmpty){
        return moveTo(getX(), getY() - 1, requireEmpty);
    }

    public synchronized boolean moveDown(){
        return moveDown(true);
    }

    public synchronized boolean moveDown(boolean requireEmpty){
        return moveTo(getX(), getY() + 1, requireEmpty);
    }

    public synchronized boolean moveLeft(){
        return moveLeft(true);
    }

    public synchronized boolean moveLeft(boolean requireEmpty){
        return moveTo(getX() - 1, getY(), requireEmpty);
    }

    public synchronized boolean moveRight(){
        return moveRight(true);
    }

    public synchronized boolean moveRight(boolean requireEmpty){
        return moveTo(getX() + 1, getY(), requireEmpty);
    }

    public synchronized boolean move(Direction direction, boolean requireEmpty){
        switch (direction){
            case UP: return moveUp(requireEmpty);
            case DOWN: return moveDown(requireEmpty);
            case LEFT: return moveLeft(requireEmpty);
            case RIGHT: return moveRight(requireEmpty);
            default: assert false;
        }
        return false;
    }

    private synchronized boolean randomMove(){
        int i = Utils.getRandom(4);
        boolean suc = false;
        for(int n = 0; n < 4; n++){
            i = (i + 1) % 4;
            switch (i){
                case 0: suc = moveUp(); break;
                case 1: suc = moveRight(); break;
                case 2: suc = moveDown(); break;
                case 3: suc = moveLeft(); break;
            }
            if(suc) break;
        }
        return suc;
        /*boolean suc = false;
        Collections.shuffle(moves);
        for(Callable<Boolean> move: moves){
            try{
                if(move.call()) {suc = true; break;}
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return suc;*/
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree){
        this.degree = degree;
    }

    private int unsuccessfulCnt = 0;

    public BasicCreature(final Image image){
        super(ItemTypeId.CREATURE, image);
        this.setOnRemove(new EventHandler<RemoveEvent>() {
            public void handle(RemoveEvent event) {
                event.getConsole().add(new DeadCreature(image, getX(), getY()));
            }
        });

        new AssignmentPoolTask(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                if(isRemoved() || isLoading()) return false;
                ChessboardCreature target2 = getTarget();
                ChessboardGameConsole curConsole = getConsole();
                Animation animation = moveAnimation;

                if(target2 == null || target2.isRemoved()){
                    setTarget(null);
                    setEmitEnabled(false);
                    return true;
                }

                if(Math.ceil(Utils.calculateDistance(getX(), getY(), target2.getX(), target2.getY())) <= interactingRadian) {
                    setEmitEnabled(true);
                    degree = Utils.calculateDegrees(getX(), getY(), target2.getX(), target2.getY());
                    if(animation == null || animation.getStatus() == Animation.Status.STOPPED) randomMove();
                }
                else if(curConsole != null){
                    curConsole.getLock().lock();
                    setEmitEnabled(false);

                    if (animation == null || animation.getStatus() == Animation.Status.STOPPED) {
                        Direction direction = Utils.getDirection(getX(), getY(), target2.getX(), target2.getY());

                        boolean suc = false;
                        switch (direction) {
                            case UP:
                                if (moveUp()) suc = true;
                                break;
                            case UPRIGHT:
                                if (moveUp() || moveRight()) suc = true;
                                break;
                            case RIGHT:
                                if (moveRight()) suc = true;
                                break;
                            case DOWNRIGHT:
                                if (moveDown() || moveRight()) suc = true;
                                break;
                            case DOWN:
                                if (moveDown()) suc = true;
                                break;
                            case DOWNLEFT:
                                if (moveDown() || moveLeft()) suc = true;
                                break;
                            case LEFT:
                                if (moveLeft()) suc = true;
                                break;
                            case UPLEFT:
                                if (moveUp() || moveLeft()) suc = true;
                                break;
                            default:
                                assert false;
                        }

                        if(!suc){
                            unsuccessfulCnt++;
                            if(unsuccessfulCnt >= 3){
                                unsuccessfulCnt = 0;
                                setTarget(null);
                            }
                            else randomMove();
                        }
                    }
                    curConsole.getLock().unlock();
                }
                return true;
            }
        }, Duration.millis(500), -1).run();
    }

    public void setSpeedRate(double speedRate) {
        this.speedRate = speedRate;
    }

    public void setInteractingRadian(double radian){
        this.interactingRadian = radian;
    }

    private Animation getMoveAnimation(){
        return moveAnimation;
    }

    private void makeMoveAnimation(int dstGridX, int dstGridY){
        ChessboardGameConsole console = getConsole();
        if(console == null) return;
        assert(dstGridX >= 0 && dstGridY >= 0);
        Grid dstGrid = getConsole().getGameMap().getGridByGridPos(dstGridX, dstGridY);
        Point2D pos = new Point2D(dstGrid.getNode().getBoundsInParent().getMinX(), dstGrid.getNode().getBoundsInParent().getMinY());
        KeyValue xValue = new KeyValue(getNode().layoutXProperty(), pos.getX());
        KeyValue yValue = new KeyValue(getNode().layoutYProperty(), pos.getY());
        KeyFrame frame = new KeyFrame(Duration.millis(1000 / speedRate), xValue, yValue);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(frame);
        if(moveAnimation != null) {
            moveAnimation.stop();
            getAnimations().remove(moveAnimation);
        }
        moveAnimation = timeline;
        getAnimations().add(moveAnimation);
        timeline.play();
    }


    public int getX() {
        return chessboardCreatureComponent.getX();
    }


    public void setX(int x) {
        chessboardCreatureComponent.setX(x);
    }


    public int getY() {
        return chessboardCreatureComponent.getY();
    }


    public void setY(int y) {
        chessboardCreatureComponent.setY(y);
    }

    @Override
    public void setConsole(GameConsole console) {
        assert console instanceof ChessboardGameConsole;
        super.setConsole(console);
    }

    @Override
    public ChessboardGameConsole getConsole() {
        return ((ChessboardGameConsole) super.getConsole());
    }

    public ChessboardCreature getTarget(){
        return target;
    }

    public void setTarget(ChessboardCreature target){
        this.target = isLoading() ? null : target;
    }


    public void acceptInteract(Effect effect) {
        effect.make();
        if(effect.getSource() instanceof Bullet && ((Bullet) effect.getSource()).getEmitter() instanceof BasicCreature){
            setTarget((BasicCreature) ((Bullet) effect.getSource()).getEmitter());
        }
    }


    public Rectangle2D getInteractedRange() {
        return getCurrentBounds();
    }

    public abstract void setDamage(double damage);

    public abstract double getDamage();
}
