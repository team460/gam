package game.message.toServer;

import game.model.game.model.ServerGameModel;
import game.model.game.model.team.TeamResourceData;
import game.model.game.model.worldObject.entity.Entity;

public class SellEntityRequest implements ToServerRequest {
  /**
   * Entity to be deleted.
   */
  private Entity entity;

  public SellEntityRequest() {

  }

  /**
   * Constructor of the request, given an entity to be made.
   * @param entity entity to be made
   */
  public SellEntityRequest(Entity entity) {
    this.entity = entity;
  }

  /**
   * Creates the entity in the server.
   * @param model the game model on the game server
   */
  @Override
  public void executeServer(ServerGameModel model) {
    System.out.println("deleting, level: " + entity.<Integer>get(Entity.EntityProperty.LEVEL));
    if (entity.has(Entity.EntityProperty.LEVEL)) {
      model.changeResource(entity.getTeam(), TeamResourceData.levelToResource.get(entity.<Integer>get(Entity.EntityProperty.LEVEL)), 8);
      model.removeByID(entity.id);
    }
  }
}
