package game.view;

import game.model.game.model.ClientGameModel;
import javafx.scene.ImageCursor;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import util.Const;

import static images.Images.CURSOR_IMAGE;

public class FPSPanel extends Region {
    private Text text;

    public FPSPanel(ClientGameModel model) {
        this.setCursor(new ImageCursor(CURSOR_IMAGE, 0, 0));
        this.setBackground(model.getTeam().getPanelBG());
        text = new Text();
        text.setFont(new Font(20));
        text.setFill(Color.WHITE);
        text.setLayoutX(15);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setLayoutY(30);
        this.getChildren().add(text);
    }

    public void setFPS(int fps, double time) {
        int minutes;
        int seconds;
        String minute;
        String second;

        minutes = (int) time / 60;
        seconds = (int) time % 60;
        minute = (minutes < 10) ? "0" + minutes : "" + minutes;
        second = (seconds < 10) ? "0" + seconds : "" + seconds;

        text.setText(fps + ", " + minute + ":" + second);
    }
}
