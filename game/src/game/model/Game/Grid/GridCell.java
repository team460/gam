package game.model.Game.Grid;

import game.model.Game.GameModel;
import game.model.Game.Tile.Tile;
import game.model.Game.WorldObject.Blocker;
import game.model.Game.WorldObject.Entity;
import javafx.scene.canvas.GraphicsContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GridCell {

    private Set<Entity> contains = new HashSet<>();

    //x,y coordiante of top left of this part of the grid
    public int x, y;

    private Tile type;

    //is passible if it is passible tile
    public boolean isPassable() {
        return type.isPassable();
    }

    public Set<Entity> getContains() {
        return Collections.synchronizedSet(contains);
    }

    public void add(Entity o) {
        contains.add(o);
    }

    public void remove(Entity o) {
        contains.remove(o);
    }

    public GridCell(GameModel model, int x, int y, Tile type) {
        this.x = x;
        this.y = y;
        this.type = type;
        if(!isPassable())
            add(new Blocker(model, x,y));
    }

    public void drawBackground(GraphicsContext gc) {
        type.draw(gc, x, y);
    }

    public void collideContents(GameModel model) {
        for(Entity a : contains)
            for (Entity b : contains)
                if (a != b ) // && a.getShape().testCollision(b.getShape()))
                    a.collision(model, b);
    }

    public Tile getTile() {
        return type;
    }

    public void setType(Tile type) {
        this.type = type;
    }

    public void removeByID(UUID entityID) {
        contains.removeIf(o -> o.getUuid().equals(entityID));
    }
}