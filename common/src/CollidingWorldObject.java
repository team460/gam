import javafx.scene.canvas.GraphicsContext;

import java.util.Set;

public abstract class CollidingWorldObject<T extends CollidingWorldObject.PositionWorldObjectData> extends WorldObject<T> {

    @Override
    public void draw(GraphicsContext gc) {
        data.shape.draw(gc);
    }

    public static class PositionWorldObjectData<T extends CollidingWorldObject> extends WorldObjectData<T> {
        Shape shape;
    }

    CollidingWorldObject(Shape shape, GameModel model) {
        this.data.shape = shape;
        this.tiles = data.shape.getTiles(model.map);
    }

    @Override
    public void set(T t) {
        data.shape = t.shape;
    }

    @Override
    public void update(long time, GameModel model) {
        //do nothing
    }

    @Override
    public void update(GameModel model) {
        super.update(model);
        this.tiles = data.shape.getTiles(model.map);
    }

    /**
     * called whenever collides with another object
     * called for both directions
     * for now it is required that after this call they will not be colliding
     */
    void collision(CollidingWorldObject<?> other) {
        //Do nothing
    }
    //set of tiles it is on
    private Set<Tile> tiles;


    /**
     * should return true exactly if the two objects are colldiding
     * type dependent- ew!
     */
    static boolean testCollision(CollidingWorldObject<?> a, CollidingWorldObject<?> b)  {
        if(Util.setsDisjoint(a.tiles, b.tiles))
            return false;
        return a.data.shape.testCollision(b.data.shape);throw new RuntimeException("all Position WorldObjects must either be circle or rectangle objects!");
    }
}
