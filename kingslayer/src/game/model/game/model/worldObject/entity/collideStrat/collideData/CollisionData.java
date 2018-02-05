package game.model.game.model.worldObject.entity.collideStrat.collideData;

import game.model.game.model.worldObject.entity.Entity;
import game.model.game.model.worldObject.entity.EntityData;
import util.Util;
import game.model.game.model.GameModel;
import game.model.game.grid.GridCell;
import javafx.scene.canvas.GraphicsContext;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.PI;
import static util.Const.*;
import static util.Util.dist;

/**
 * Shouldn't be drawable! Purely for testing for now.
 * Abstract class that defines a generic collisionData. All entities on a tile
 * have a collisionData derived from this class.
 */
public abstract class CollisionData {

    /**
     * Return the set of all cells this collisionData overlaps with. This method
     * should return a new set every time.
     * @param gameMap current model of the game
     * @return the set of all cells that this collisionData overlaps with.
     */
    public abstract Set<GridCell> getCells(Entity entity, GameModel gameMap);

    transient double prevX = -1;
    transient double prevY = -1;

    /**
     * Return the set of all cell references this collisionData overlaps with.
     * This method should return a new set every time.
     */
    void updateCells(Entity entity, GameModel model) {
        if(entity.data.x != prevX || entity.data.y != prevY) {
            Set<GridCell> afterSet = entity.data.collisionData.getCells(entity, model);

            if (entity.containedIn != null)
                for (GridCell cell : entity.containedIn)
                    if (!afterSet.contains(cell))
                        cell.removeContents(entity);
            for (GridCell cell : afterSet)
                cell.addContents(entity);

            entity.containedIn = afterSet;
            prevX = entity.data.x;
            prevY = entity.data.y;
        }
    }

    /**
     * Returns true if this collisionData collides with the given collisionData. Returns false
     * otherwise.
     * @return true if this collisionData collides with the specified collisionData
     */
    public static boolean testCollision(Entity t, Entity o) {
        //TODO is this right?
        double angle = Util.angle2Points(t.data.x, t.data.y, o.data.x, o.data.y);
        return t.data.collisionData.getRadius(angle) + o.data.collisionData.getRadius(angle + PI) < dist(t.data.x, t.data.y, o.data.x, o.data.y);
    }

    /**
     * for debugging hitboxes.
     */
    public abstract void draw(GraphicsContext gc, Entity entity);

    /*
    gets the distance from the center of the collisionData to the farthest edge at the given angle
     */
    public abstract double getRadius(double angle);

    /*
    the width of the object through its horizontal center line
     */
    public double getWidth() {
        return getRadius(ANGLE_LEFT) + getRadius(ANGLE_RIGHT);
    }

    /*
    the width of the object through its vertical center line
     */
    public double getHeight() {
        return getRadius(ANGLE_UP) + getRadius(ANGLE_DOWN);
    }
}
