package game.model.game.grid;

import game.model.game.model.GameModel;
import game.model.game.map.Tile;
import game.model.game.model.worldObject.entity.Entity;
import game.model.game.model.worldObject.entity.collideStrat.CollisionStrat;
import game.model.game.model.worldObject.entity.collideStrat.hitbox.Hitbox;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.util.*;
import java.util.List;

import static images.Images.TILE_IMAGE;
import static util.Const.TILE_PIXELS;
import static util.Util.toDrawCoords;

/**
 * Defines an individual cell on the game grid. Knows the entities
 * that currently exist on the cell and the type of tile that it
 * currently is.
 */
public class GridCell {

    /**
     * Set of entities that currently reside on the cell.
     */
    private ArrayList<Entity> contents = new ArrayList<>();

    /**
     * X-coordinate of the top left of this cell.
     */
    private int x;

    /**
     * Y-coordinate of the top left of this cell.
     */
    private int y;

    /**
     * Type of tile that the grid is currently.
     */
    private Tile tile;

    /**
     * Returns true if the cell is able to be passed through, or equivalently,
     * if a pathing enemy should try to go through this tile. A cell is
     * considered unpassable f it has a cell hitbox occupying it.
     * @return true if the cell is able to be walked through
     */
    public boolean isPassable(GameModel model) {
        return contents.stream().noneMatch(e -> e.getCollideType() == CollisionStrat.CollideType.HARD && e.containedIn.contains(model.getCell(x, y)));
    }

    /**
     * Gets the contents of the cell.
     * @return the current contents of the cell
     */
    public Collection<Entity> getContents() {
        return contents;
    }

    /**
     * Adds the specified entity to this cell.
     * @param o the entity to be added to this cell
     */
    public void addContents(Entity o) {
        contents.add(o);
    }

    /**
     * Removes the specified entity from this cell.
     * @param o the entity to be removed from this cell
     */
    public void removeContents(Entity o) {
        contents.remove(o);
    }

    /**
     * Constructor for a grid cell.
     * @param x x-coordinate of the top left of the cell
     * @param y y-coordinate of the top left of the cell
     */
    public GridCell(int x, int y) {
        this.x = x;
        this.y = y;
        this.tile = Tile.UNSET;
    }

    /**
     * Default constrcuctor needed for serialization.
     */
    public GridCell() {

    }


    private static Map<String, Point> TILE_MAP;
    private static Map<Character, List<Character>> matches;
    private static final int TILE_IMAGE_TILE_SIZE = 32;

    static {

        TILE_MAP = new HashMap<>();
        matches = new HashMap<>();

        Scanner input = new Scanner(Tile.class.getResourceAsStream("tile_map.txt"));

        while (input.hasNext()) {
            Point p = new Point(input.nextInt(), input.nextInt());
            if (p.x == -1)
                break;
            input.nextLine();
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < 3; i++)
                str.append(input.nextLine());
            input.nextLine();
            TILE_MAP.put(str.toString(), p);
        }

        matches.put('G', Arrays.asList('G', 'E', 'X', 'L', 'K', '_'));
        matches.put('D', Arrays.asList('D', 'E', 'U', 'L', 'K', '_'));
        matches.put('W', Arrays.asList('W', 'J', 'X', 'U', 'L', '_'));
        matches.put('B', Arrays.asList('B', 'J', 'X', 'U', 'E', '_'));
    }

    private Point maxPoint = new Point(0, 0);

    public void initDraw(GameModel model) {

        StringBuilder hashKey = new StringBuilder();

        for (int j = -1; j < 2; j++)
            for (int i = -1; i < 2; i++)
                hashKey.append(model.getTile(x + i, y + j).tupleNum);

        int max = -1;
        for (String key : TILE_MAP.keySet()) {

            if (key.charAt(4) != hashKey.charAt(4)) // check that middle tile matches
                continue;

            int cur = 0;
            Point curPoint = TILE_MAP.get(key);

            for (int l = 0; l < 9; l++)
                if (matches.get(hashKey.charAt(l)).contains(key.charAt(l))) // check that these two are a possible match
                    cur++;

            if (cur > max) {
                max = cur;
                maxPoint = curPoint;
            }
        }
    }

    /**
         * Draws the tile in a specified cell on the map.
         *
         * @param gc context used to drawFG the tile
         */
        public void draw(GraphicsContext gc, GameModel model, boolean firstAnimation) {
            if (!firstAnimation && this.tile.tupleNum == 'W')
                gc.drawImage(TILE_IMAGE,
                    (maxPoint.x + 10) * TILE_IMAGE_TILE_SIZE,
                    maxPoint.y * TILE_IMAGE_TILE_SIZE, TILE_IMAGE_TILE_SIZE, TILE_IMAGE_TILE_SIZE,
                    toDrawCoords(x),
                    toDrawCoords(y),
                    toDrawCoords(1),
                    toDrawCoords(1));
            else
                gc.drawImage(TILE_IMAGE,
                    maxPoint.x * TILE_IMAGE_TILE_SIZE,
                    maxPoint.y * TILE_IMAGE_TILE_SIZE,
                    TILE_IMAGE_TILE_SIZE, TILE_IMAGE_TILE_SIZE,
                    toDrawCoords(x),
                    toDrawCoords(y),
                    toDrawCoords(1),
                    toDrawCoords(1));

        }

    /**
     * Perform collisions between the current contents of the cell.
     * The collisions are handled based on the entities involved.
     * @param model current model of the game
     */
    public void collideContents(GameModel model) {
        for(int i = 0; i < contents.size(); i++) {
            for (int j = 0; j < i; j++) {
                Entity a = contents.get(i);
                Entity b = contents.get(j);
                if(Hitbox.testCollision(a, b)) {
                    a.collision(model, b);
                    b.collision(model, a);
                }
            }
        }
    }

    /**
     * Gets the current tile type of the cell.
     * @return the current tile type of the cell
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Sets the tile of the cell to the specified tile
     * @param tile the new tile type of the cell
     */
    public void setTile(Tile tile, GameModel model) {
        this.tile = tile;
    }

    /**
     * Remove all entities that have the specified id from the
     * cell.
     * @param entityID the id of the entity to be removed
     */
    public void removeByID(long entityID) {
        contents.removeIf(o -> o.id == entityID);
    }

    /**
     * Gets the x coordinate of the top left of this grid cell.
     * @return the x coordinate of the top left of this grid cell
     */
    public int getTopLeftX() {
        return x;
    }

    /**
     * Gets the x coordinate of the top left of this grid cell.
     * @return the x coordinate of the top left of this grid cell
     */
    public int getTopLeftY() {
        return y;
    }

    public double getCenterX() {
        return x + 0.5;
    }

    public double getCenterY() {
        return y + 0.5;
    }


    @Override
    public boolean equals(Object o) {
        GridCell ot = (GridCell) o;
        return x == ot.x && y == ot.y;
    }

    @Override
    public int hashCode() { return (int) (0.5 * (x + y) * (x + y + 1)) + y; }
}