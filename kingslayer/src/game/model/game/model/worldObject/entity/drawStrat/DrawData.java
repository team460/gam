package game.model.game.model.worldObject.entity.drawStrat;

/*
    data to be used by the drawing strat
    not needed by a static image drawer, but would be needed for animations
 */
public class DrawData {
  boolean animated;
  int imageNum;
  char direction;

  public DrawData() {

  }

  private DrawData(boolean animated) {
    this.animated = animated;
    imageNum = 0;
    direction = 'S';
  }

  public static DrawData makeAnimated() {
    return new DrawData(true);
  }
}