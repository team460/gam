package game.model.Game.WorldObject.Entity;

import game.model.Game.Map.Tile;
import game.model.Game.Model.GameModel;
import game.model.Game.WorldObject.Team;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static Util.Const.TILE_PIXELS;

/**
 * Defines the slayer of a team.
 */
public class SlayerPlayer extends Player {

    /**
     * Image of the slayer for the red team.
     */
    private static Image imageRedSlayer;

    /**
     * Image of the slayer for the blue team.
     */
    private static Image imageBlueSlayer;

    /**
     * Key for figuring out which piece of the character sheet is needed for
     * the slayer at a given point in time.
     */
    private static Map<String, Point> imageMap = new HashMap<>();

    static {
        // Read in file detailing which images in the sheet are used at what times
        Scanner input;
        try {
            input = new Scanner(Tile.class.getResource("players.txt").openStream());

            while (input.hasNext()) {
                Point curPoint = new Point(input.nextInt(), input.nextInt());
                if (curPoint.x == -1) break;
                input.nextLine();
                String info = input.nextLine();
                imageMap.put(info, curPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Gets the sprite sheets to represent the two slayers
        try {
            imageRedSlayer = new Image(Tile.class.getResource("slayer_red_sheet.png").openStream());
            imageBlueSlayer = new Image(Tile.class.getResource("slayer_blue_sheet.png").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(GraphicsContext gc, GameModel model) {
        try {
            Point p = imageMap.get(imageNum + "" + direction);
            if (this.getTeam() == Team.ONE) {
                super.draw(gc, imageRedSlayer, p);
            } else {
                super.draw(gc, imageBlueSlayer, p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
