package game.message;

import game.model.game.model.ClientGameModel;
import game.model.game.model.worldObject.entity.Entity;

/**
 * Message sent to set a player on a client's game model. This message
 * is sent by the server.
 */
public class SetPlayerMessage implements ToClientMessage {

    /**
     * ID of the player.
     */
    private long playerId;

    /**
     * Default constructor needed for serialization.
     */
    public SetPlayerMessage() {

    }

    /**
     * Constructor for a message, given a player ID.
     * @param playerId ID of the player
     */
    public SetPlayerMessage(long playerId) {
        this.playerId = playerId;
    }

    /**
     * Constructor for a message, given a player.
     * @param player the player to be set
     */
    public SetPlayerMessage(Entity player) {
        this.playerId = player.id;
    }

    /**
     * Sets the player in the client's game model.
     * @param model the game model on the client
     */
    @Override
    public void executeClient(ClientGameModel model) {
        model.setLocalPlayer(playerId);
    }
}
