package game.model.Game.WorldObject.Entity;

import Util.Util;
import game.model.Game.Grid.GridCell;
import game.model.Game.Model.GameModel;
import game.model.Game.WorldObject.Shape.*;
import game.model.Game.WorldObject.Shape.Shape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;

import static Util.Const.TILE_PIXELS;
import static java.lang.Math.PI;

/**
 * Class that defines an abstract player in the game world.
 */
public abstract class Player extends MovingEntity {

    /**
     * Shape that represents a player in the game.
     */
    private CircleShape shape;

    /**
     * Keeps track of change in x direction. Used for
     * calculating movement angle.
     */
    private int dx;

    /**
     * Keeps track of change in y direction. Used for
     * calculating movement angle.
     */
    private int dy;

    /**
     * Flag that says if the player is currently moving up.
     */
    private boolean up = false;

    /**
     * Flag that says if the player is currently moving left.
     */
    private boolean left = false;

    /**
     * Flag that says if the player is currently moving right.
     */
    private boolean right = false;

    /**
     * Flag that says if the player is currently moving down.
     */
    private boolean down = false;

    /**
     * Index to get the image for a certain frame.
     */
    int imageNum = 0;

    /**
     * Index to get the images for movement in a certain direction.
     */
    char direction = SOUTH;

    /**
     * Counter to help create animation.
     */
    private int count = 0;

    /**
     * Static variables for the four cardinal directions.
     */
    private static char NORTH = 'N';
    private static char EAST = 'E';
    private static char SOUTH = 'S';
    private static char WEST = 'W';

    /**
     * Default constructor of a player.
     */
    public Player() {
        super();
        shape = new CircleShape(0.0, 0.0, 0.3);
        this.setMovementAngle(0.5 * PI);
        // TODO set health, team
    }

    /**
     * Constructor of a player given coordinates.
     * @param model current model of the game
     * @param x x-coordinate to spawn the player
     * @param y y-coordinate to spawn the player
     * @param king flag that determines whether this player is a king TODO is this correct?
     */
    public Player(GameModel model, double x, double y, boolean king) {
        super(model);
        shape = new CircleShape(x, y, 0.3);
        this.setMovementAngle(0.5 * PI);
        // TODO set health and team
        // TODO deal with king boolean
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public double getDrawZ() {
        return getY();
    }

    @Override
    public void copyOf(Entity other) {
        assert (other instanceof Player);
        Player o = (Player) other;
        this.shape = o.shape;
        this.dx = o.dx;
        this.dy = o.dy;
        this.up = o.up;
        this.left = o.left;
        this.right = o.right;
        this.down = o.down;
        super.copyOf(other);
    }

    @Override
    public void collision(GameModel model, MovingEntity collidesWith) {
        if (collidesWith.getVelocity() == 0) {
            // TODO treat like stationary entity collision
        } else {
            // TODO other cases
        }
    }

    @Override
    public void collision(GameModel model, StationaryEntity collidesWith) {
       // System.out.println("X: " + (shape.getX()) + ", Y: " + (shape.getY()) + ", radius: " + shape.getRadius());

//        for(Shape.GridCellReference g : shape.getCellsReference())
//            System.out.println("Cell X: " + g.x + ", cell Y: " + g.y);
      //  System.out.println("Blocker X, y: " + collidesWith.getX() + ", " + collidesWith.getY());

        if (collidesWith.getShape() instanceof CellShape) {
            // Check side of collision based on angle between entity and collided object.
            double collisionAngle = Util.angle2Points(getX(), getY(), collidesWith.getX(), collidesWith.getY());

            boolean hitleft = !(collisionAngle < -3*PI/4 || collisionAngle > 3*PI/4);
            boolean hittop = !(collisionAngle > -3*PI/4 && collisionAngle < -PI/4);

            // Hit from left/right.
            boolean hitVerticalWall = collisionAngle > -PI / 4 && collisionAngle < PI / 4 ||
                    collisionAngle > 3 * PI / 4 || collisionAngle < -3 * PI / 4;

            // Hit from top/bottom.
            boolean hitHorizontalWall = collisionAngle < -PI / 4 && collisionAngle > -3 * PI / 4
                    || collisionAngle > PI / 4 && collisionAngle < 3 * PI / 4;

            if (hitVerticalWall) {
                setPos(collidesWith.getX() - (hitleft ? 1 : -1) * (0.5 + this.shape.getRadius()), shape.getY());
            }

            if (hitHorizontalWall) {
                setPos(shape.getX(), collidesWith.getY() - (hittop ? 1 : -1) * (0.5 + this.shape.getRadius()));
            }
        }
        else{

        }

        // TODO problem running into object from another direction but still hiting object

    }

    /**
     * Move the player based on the given direction.
     * @param dir direction the player wants to move
     */
    public void move(String dir) {
//        System.out.println("Movement Angle: " + getMovementAngle());
        boolean dirUpdate = false;  // Check if a direction has changed.
        switch (dir.toLowerCase()) {
            case "up" : // Upward movement.
                if(!up) {
                    up = true;
                    dy -= 1;
                    dirUpdate = true;
                }
                break;
            case "down" : // Downward movement.
                if (!down) {
                    down = true;
                    dy += 1;
                    dirUpdate = true;
                }
                break;
            case "left" : // Leftward movement.
                if(!left) {
                    left = true;
                    dx -= 1;
                    dirUpdate = true;
                }
                break;
            case "right" : // Rightward movement.
                if(!right) {
                    right = true;
                    dx += 1;
                    dirUpdate = true;
                }
                break;
            default :
                throw new RuntimeException("Unknown direction to move.");
        }
        if (dirUpdate) {    // Only update extra stuff if any direction now has movement in that direction.
            setVelocity(0.05);
            double oldAngle = getMovementAngle();
            setMovementAngle(Math.atan2(dy, dx));     // Update new movement angle after a direction has changed.
            if (up && down || left && right) {   // Directions cancelling out results in no speed, maintain angle.
                setVelocity(0);
                setMovementAngle(oldAngle);
            }
        }
    }

    /**
     * Stops the player based on the given direction.
     * @param dir direction the player wants to stop moving
     */
    public void stop(String dir) {
        switch (dir.toLowerCase()) {
            case "up" : // Stop upward movement.
                up = false;
                dy += 1;
                break;
            case "down" : // Stop downward movement.
                down = false;
                dy -= 1;
                break;
            case "left" : // Stop leftward movement.
                left = false;
                dx += 1;
                break;
            case "right" : // Stop rightward movement.
                right = false;
                dx -= 1;
                break;
            default :
                throw new RuntimeException("Unknown direction to stop.");
        }
        if (!up && !left && !right && !down)   // No movement - set speed to 0.
            this.setVelocity(0);
        else {
            this.setVelocity(0.05);
            double oldAngle = this.getMovementAngle();
            this.setMovementAngle(Math.atan2(dy, dx)); // Update new movement angle after a direction has changed.
            if (up && down || left && right) {  // Directions cancelling out results in no speed, preserve angle.
                setVelocity(0);
                setMovementAngle(oldAngle);
            }
        }
    }

    /**
     * Move the player to a specified cell.
     * @param cell cell to move to.
     */
    public void moveTo(GridCell cell) {
        setVelocity(0.05);
        setMovementAngle(Math.atan2(cell.getY() + 0.5 - this.getShape().getY(), cell.getX() + 0.5 - this.getShape().getX()));
        double movementAngle = getMovementAngle();
//        System.out.println("PLAYER: " + this.getShape().getX() + ", " + this.getShape().getY());
//        System.out.println("CELL: " + cell.getX() + ", " + cell.getY());
        while ((Math.abs(cell.getX() + 0.5 - this.getX()) > 0.05 || Math.abs(cell.getY() + 0.5 - this.getY()) > 0.05) &&
                cell.isPassable())
            updatePlayer();
        setVelocity(0);
        setMovementAngle(movementAngle);
    }

    public void updatePlayer() {
        shape.shift(getVelocityX() * 0.01, getVelocityY() * 0.01);
        double angle = getMovementAngle();
        if (angle >= -0.75 * PI && angle < -0.25 * PI) {
            direction = NORTH;
        } else if (angle >= -0.25 * PI && angle < 0.25 * PI) {
            direction = EAST;
        } else if (angle >= 0.25 * PI && angle < 0.75 * PI) {
            direction = SOUTH;
        } else if (angle >= 0.75 * PI || angle < -0.75 * PI) {
            direction = WEST;
        }

        // Update image being used
        if (this.getVelocity() != 0) {
            count++;
            if (count > 11) {
                count = 0;
                imageNum = (imageNum + 1) % 3;
            }
        } else {
            imageNum = 0;
        }
    }

    @Override
    public void update(long time, GameModel model) {
        // shape.shift(dx * time * 1e-9 * 10, dy * time * 1e-9 * 10); TODO use the delta

        shape.shift(getVelocityX() * time * 5e-8, getVelocityY() * time * 5e-8);

        //TODO check collision
//        GridCell cell = model.getCell((int) (x + shape.getRadius() * vx / Math.sqrt(vx * vx)),
//                (int) (y + shape.getRadius() * vy / Math.sqrt(vy * vy)));
//        for (Entity e : cell.getContents())
//            if (!e.equals(this) && this.testCollision(e)) {
//                setPos(x, y);
//                break;
//            }


        // Update direction of image
        double angle = getMovementAngle();
        if (angle >= -0.75 * PI && angle < -0.25 * PI) {
            direction = NORTH;
        } else if (angle >= -0.25 * PI && angle < 0.25 * PI) {
            direction = EAST;
        } else if (angle >= 0.25 * PI && angle < 0.75 * PI) {
            direction = SOUTH;
        } else if (angle >= 0.75 * PI || angle < -0.75 * PI) {
            direction = WEST;
        }

        // Update image being used
        if (this.getVelocity() != 0) {
            count++;
            if (count > 11) {
                count = 0;
                imageNum = (imageNum + 1) % 3;
            }
        } else {
            imageNum = 0;
        }    }

    /**
     * Draws the piece of the given image starting at point p.
     * @param gc graphics context image is to be drawn with
     * @param image a character sheet for the player being drawn
     * @param p the top left point of where the desired piece of the image is
     */
    public void draw(GraphicsContext gc, Image image, Point p) {
        gc.drawImage(image,
            p.x * 32, p.y * 32, 32, 32,
            this.getX() * TILE_PIXELS - TILE_PIXELS / 2, this.getY() * TILE_PIXELS - TILE_PIXELS / 2 + 25, TILE_PIXELS, TILE_PIXELS);
    }
}
