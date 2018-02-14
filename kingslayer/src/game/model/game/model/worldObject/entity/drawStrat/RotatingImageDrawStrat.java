package game.model.game.model.worldObject.entity.drawStrat;

import game.model.game.model.worldObject.entity.Entity;
import images.Images;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.Util;

import static util.Util.toDrawCoords;

public abstract class RotatingImageDrawStrat extends ImageDrawStrat {
    public static final ImageDrawStrat ARROW_IMAGE_DRAW_STRAT = new ArrowImageDrawStrat();
    private RotatingImageDrawStrat() {
        super();
    }


    public void draw(Entity entity, GraphicsContext gc) {
        double w = this.getWidth();
        double h = this.getHeight();
        double x = w - this.getCenterX();
        double y = h - this.getCenterY();

        Util.drawRotatedImage(gc,
            getImage(),
            entity.data.updateData.velocity.getAngle(),
            toDrawCoords(entity.data.x - x),
            toDrawCoords(entity.data.y - y),
            toDrawCoords(w),
            toDrawCoords(h));
    }


    public static class ArrowImageDrawStrat extends RotatingImageDrawStrat {

        public static final DrawStrat SINGLETON = new ArrowImageDrawStrat();

        @Override
        Image getImage() {
            return Images.ARROW_IMAGE;
        }

        @Override
        double getWidth() {
            return 0.4;
        }

        @Override
        double getHeight() {
            return 0.4;
        }

        @Override
        double getCenterX() {
            return 0.2;
        }

        @Override
        double getCenterY() {
            return 0.2;
        }

        public ArrowImageDrawStrat() {

        }
    }
}
