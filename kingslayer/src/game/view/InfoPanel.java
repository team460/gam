package game.view;

import game.model.game.model.ClientGameModel;
import game.model.game.model.GameModel;
import javafx.scene.ImageCursor;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static images.Images.CURSOR_IMAGE;

/**
 * panel in bottem right for displaying teams health, xp and ??
 */
public class InfoPanel extends Region  {
    private GameModel model;
public InfoPanel(ClientGameModel model){
    this.model = model;
    this.setCursor(new ImageCursor(CURSOR_IMAGE, 0, 0));
    this.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(10))));
    this.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(3), null)));
    Text text = new Text("KING HP: 100     SLAYER HP: 100   SLAYER XP: 0    SLAYER LEVEL: 1");
    text.setFont(new Font(20));
    text.setFill(Color.WHITE);
    text.setLayoutX(10);
    text.setTextAlignment(TextAlignment.CENTER);
    text.setLayoutY(50);
    this.getChildren().add(text);
}
}
