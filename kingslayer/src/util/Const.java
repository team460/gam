package util;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static java.lang.Math.PI;

/**
 * Abstract class that defines various constants to be used.
 */
public abstract class Const {

    public static boolean FIXSEED = false;

    public static final double SCREEN_DIAG_STD_SIZE = 800;
    public static final double MAX_ZOOM = 2;
    public static final double MIN_ZOOM = 0.8;
    public static final Paint PANEL_BG_COLOR_RED = Color.color(0.7,0.0,0.0,0.6);
    public static final Background PANEL_BG_RED = new Background(new BackgroundFill(PANEL_BG_COLOR_RED, new CornerRadii(10), null));
    public static final Paint PANEL_BG_COLOR_BLUE = Color.color(0.0,0.0,0.8,0.6);
    public static final Background PANEL_BG_BLUE = new Background(new BackgroundFill(PANEL_BG_COLOR_BLUE, new CornerRadii(10), null));
    public static boolean FPSPrint = true;

    public static final int SECONDS_TO_SLAYER_MANA_REGEN_UP = 45;
    public static final double AMOUNT_SLAYER_MANA_REGEN_UP = 0.25;

    /**
     * Flag that signals whether to update in debugging mdode.
     */
    public static boolean DEBUG_DRAW = false;

    /**
     * Angles based on the drawing coordinate system.
     */
    public static final double ANGLE_RIGHT = 0;
    public static final double ANGLE_LEFT = PI;
    public static final double ANGLE_DOWN = PI / 2;
    public static final double ANGLE_UP = -PI / 2;
    public static final double ANGLE_UP_RIGHT = -PI / 4;
    public static final double ANGLE_UP_LEFT = -3 * PI / 4;
    public static final double ANGLE_DOWN_RIGHT = PI / 4;
    public static final double ANGLE_DOWN_LEFT = 3 * PI / 4;

    /**
     * Conversions between nano seconds and seconds, and vice versa.
     */
    public static final double NANOS_TO_SECONDS = 1e-9;
    public static final double SECONDS_TO_NANOS = 1e9;

    /**
     * Number of teams and roles that currently are supported.
     */
    public static final int NUM_TEAMS = 3;
    public static final int NUM_ROLES = 3;

    /**
     * Width of the game map, in terms of number of grid cells.
     */
    public final static int GRID_X_SIZE = 80;

    /**
     * Height of the game map, in terms of number of grid cells.
     */
    public final static int GRID_Y_SIZE = 80;

    /**
     * Width of the initial game screen.
     */
    public final static int INIT_SCREEN_WIDTH = 800;

    /**
     * Height of the initial game screen.
     */
    public final static int INIT_SCREEN_HEIGHT = 600;

    /**
     * Number of pixels on the side of a tile.
     */
    public final static int TILE_PIXELS = 32;

    /**
     * Number of pixels on the width of the canvas.
     */
    public final static int CANVAS_WIDTH = GRID_X_SIZE * TILE_PIXELS;

    /**
     * Number of pixels on the height of the canvas.
     */
    public final static int CANVAS_HEIGHT = GRID_Y_SIZE * TILE_PIXELS;

    /**
     * Number of updates per second.
     * Should equal 1e9 / UPDATE_LOOP_TIME_NANOS.
     */
    public final static int UPDATES_PER_SECOND = 20;

    /**
     * The time period between different water animations.
     */
    public static final int WATER_ANIM_PERIOD = UPDATES_PER_SECOND * 3;

    public final static int AI_LOOP_UPDATE_PER_FRAMES = 15;
    public final static int FOG_UPDATE_PER_FRAMES = 5;

    // TODO balancing
    public final static int STARTING_WOOD = 15;
    public final static int STARTING_STONE = 25;
    public final static int STARTING_METAL = 40;

    public static final int FIRST_LEVEL_WOOD_COLLECTED = 3;
    public static final int SECOND_LEVEL_WOOD_COLLECTED = 10;
    public static final int THIRD_LEVEL_WOOD_COLLECTED = 15;

    public static final int SECOND_LEVEL_STONE_COLLECTED = 4;
    public static final int THIRD_LEVEL_STONE_COLLECTED = 10;

    public static final int THIRD_LEVEL_METAL_COLLECTED = 5;
}
