package game.model.game.model.worldObject.entity.collideStrat;

import game.model.game.model.GameModel;
import game.model.game.model.worldObject.entity.Entity;

/**
 * Defines how projectiles, like arrows, collide with other entities.
 */
public abstract class ProjectileCollisionStrat extends CollisionStrat {

    public enum Projectile {
        ARROW
    }

    /**
     * Performs the soft collision of entity a with entity b in the game model:
     * b is given to have a soft collision type. The collision strategy used
     * is the strategy of entity a: this only performs the handling for entity a.
     * This usually results in some sort of damaging of entity b.
     * @param model current model of the game
     * @param a entity to perform collision handling on
     * @param b entity colliding with
     */
    public abstract void collisionSoft(GameModel model, Entity a, Entity b);

    /**
     * Performs the water collision of entity a with entity b in the game model:
     * b is given to have a water collision type. The collision strategy used
     * is the strategy of entity a: this only performs the handling for entity a.
     * This does nothing unless the water being collided with is the boundary of
     * the game.
     * @param model current model of the game
     * @param a entity to perform collision handling on
     * @param b entity colliding with
     */
    public abstract void collisionWater(GameModel model, Entity a, Entity b);

    /**
     * Performs the hard collision of entity a with entity b in the game model:
     * b is given to have a hard collision type. The collision strategy used
     * is the strategy of entity a: this only performs the handling for entity a.
     * This usually results in the removal of entity a and nothing to entity b,
     * unless it can be damaged.
     * @param model current model of the game
     * @param a entity to perform collision handling on
     * @param b entity colliding with
     */
    protected abstract void collisionHard(GameModel model, Entity a, Entity b);

    /**
     * Performs the collision of entity a with entity b in the game model.
     * Entity a should perform collisions based on the type of collision type
     * that b has.
     * @param model current model of the game
     * @param a entity to perform collision handling on
     * @param b entity colliding with
     */
    public final void collision(GameModel model, Entity a, Entity b) {
        if(b.getCollideType() == CollideType.HARD) {
            collisionHard(model, a, b);
        } else if(b.getCollideType() == CollideType.SOFT) {
            collisionSoft(model, a, b);
        } else if (b.getCollideType() == CollideType.WATER) {
            collisionWater(model, a, b);
        }
    }

    @Override
    public final CollideType getCollideType() {
        return CollideType.PROJECTILE;
    }
}
