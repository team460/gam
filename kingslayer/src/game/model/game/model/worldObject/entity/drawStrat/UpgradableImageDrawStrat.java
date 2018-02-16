package game.model.game.model.worldObject.entity.drawStrat;

import game.model.game.model.worldObject.entity.Entity;
import images.Images;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static util.Util.toDrawCoords;

public class UpgradableImageDrawStrat extends ImageDrawStrat {

  public static final RedResourceCollectorImageDrawStrat RED_RESOURCE_COLLECTOR_IMAGE_DRAW_STRAT = new RedResourceCollectorImageDrawStrat();
  public static final BlueResourceCollectorImageDrawStrat BLUE_RESOURCE_COLLECTOR_IMAGE_DRAW_STRAT = new BlueResourceCollectorImageDrawStrat();

  private int tier = 0;

  @Override
  public void draw(Entity entity, GraphicsContext gc) {
    double w = this.getWidth();
    double h = this.getHeight();
    double x = w - this.getCenterX();
    double y = h - this.getCenterY();

    gc.drawImage(getImage(),
        toDrawCoords(w) * tier,
        0,
        toDrawCoords(w),
        toDrawCoords(h),
        toDrawCoords(entity.data.x - x),
        toDrawCoords(entity.data.y - y),
        toDrawCoords(w),
        toDrawCoords(h));
  }

  public void upgrade() {
    tier++;
  }

  @Override
  Image getImage() {
    return null;
  }

  @Override
  double getWidth() {
    return 1;
  }

  @Override
  double getHeight() {
    return 1.75;
  }

  @Override
  double getCenterX() {
    return 0.5;
  }

  @Override
  double getCenterY() {
    return 0.5;
  }

  public static class RedResourceCollectorImageDrawStrat extends UpgradableImageDrawStrat {
    @Override
    Image getImage() {
      return Images.RED_RESOURCE_COLLECTORS_IMAGE;
    }

    private RedResourceCollectorImageDrawStrat() {
    }
  }

  public static class BlueResourceCollectorImageDrawStrat extends UpgradableImageDrawStrat {
    @Override
    Image getImage() {
      return Images.RED_RESOURCE_COLLECTORS_IMAGE;
    }

    private BlueResourceCollectorImageDrawStrat() {
    }
  }
}