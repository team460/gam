package game.message.playerMoveMessage;

import game.message.ActionMessage;
import game.message.SetEntityMessage;
import game.model.Game.Model.ServerGameModel;
import game.model.Game.WorldObject.Entity.TestPlayer;

import com.esotericsoftware.minlog.Log;
/**
 * Message sent by a client to tell the server to move the player
 * rightwards on the game map.
 */
public class PlayerRight extends ActionMessage {

    /**
     * ID to distinguish player that sent the message.
     */
    private long id;

    /**
     * Constructor for the move message.
     * @param id player ID that send the message
     */
    public PlayerRight(long id) {
        super();
        this.id = id;
    }

    /**
     * Default constructor needed for serialization.
     */
    public PlayerRight(){

    }

    /**
     * Moves the player rightwards.
     * @param model the game model on the game server
     */
    @Override
    public void executeServer(ServerGameModel model) {
        ((TestPlayer) model.getEntityById(id)).right();
        model.processMessage(new SetEntityMessage(model.getEntityById(id)));
    }
}
