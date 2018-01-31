package game.message;

import game.model.Game.Model.ClientGameModel;
import game.model.Game.WorldObject.Entity.Entity;

/**
 * Message sent to remove an entity from a client's game model. This message
 * is sent by the server.
 */
public class RemoveEntityMessage implements ToClientMessage {

    /**
     * ID of the entity to be removed.
     */
    private long entityID;

    /**
     * Default constructor needed for serialization.
     */
    public RemoveEntityMessage() {}

    /**
     * Constructor of a message, given an entity to be removed.
     * @param entity entity to be removed
     */
    public RemoveEntityMessage(Entity entity) {
        this.entityID = entity.getId();
    }

    /**
     * Remove the references of the local entity with the given ID in the client
     * model.
     * @param model the game model on the client.
     */
    @Override
    public void executeClient(ClientGameModel model) {
        model.removeByID(entityID);
    }
}
