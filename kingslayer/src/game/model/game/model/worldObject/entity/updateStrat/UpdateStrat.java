package game.model.game.model.worldObject.entity.updateStrat;

import game.model.game.model.GameModel;
import game.model.game.model.Model;
import game.model.game.model.worldObject.entity.Entity;

import static util.Const.NANOS_TO_SECONDS;

public abstract class UpdateStrat {

    public UpdateData initUpdateData() {
        return new UpdateData();
    }

    /**
     * Updates the entity in the game model.
     * @param model current game model
     */
    public void update(Entity entity, GameModel model) {
        if(entity.data.updateData.last_update == -1) {
            entity.data.updateData.last_update = model.nanoTime();
            return;
        }
        long current_time = model.nanoTime();
        update(entity, model, NANOS_TO_SECONDS * (current_time - entity.data.updateData.last_update));
        entity.data.updateData.last_update = current_time;
    }

    protected abstract void update(Entity entity, GameModel model, double seconds);
}
