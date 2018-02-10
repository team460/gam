package game.model.game.model.worldObject.entity.drawStrat;

import game.model.game.model.worldObject.entity.Entity;
import game.model.game.model.worldObject.entity.EntityData;
import javafx.scene.canvas.GraphicsContext;

public class NoDrawStrat extends DrawStrat {

    public static final NoDrawStrat SINGLETON = new NoDrawStrat();

    private NoDrawStrat(){};

    @Override
    public DrawData initDrawData() {
        return null;
    }

    @Override
    public void draw(Entity entity, GraphicsContext gc) { }

    @Override
    public double getDrawZ(EntityData entity) {
        return 0;
    }
}