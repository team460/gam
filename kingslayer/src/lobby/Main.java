package lobby;

import com.esotericsoftware.minlog.Log;
import game.model.game.model.team.Role;
import game.model.game.model.team.Team;
import game.singlePlayer.SingleplayerController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import network.LobbyClient;
import network.LobbyClient2LobbyAdaptor;
import network.LobbyServer;

import java.io.IOException;

import static images.Images.*;
import static util.Util.sleep;

public class Main extends Application {
    LobbyClient lobbyClient = null;
    LobbyServer lobbyServer = null;

    public Scene mainMenuScene;


    private Font font = Font.font("", FontWeight.BOLD, 36);
    private static Color textColor = Color.web("b5de0f");

    private VBox menuBox;

    private int currentItem = 0;
    private AnimationTimer animator;

    ChoiceBox<Team> teamChoice;
    ChoiceBox<Role> roleChoice;

    boolean connected = false;

    Canvas bgCanvas = new Canvas();
    GraphicsContext bgGC = bgCanvas.getGraphicsContext2D();
    Canvas midCanvas = new Canvas();
    GraphicsContext midGC = midCanvas.getGraphicsContext2D();


    MenuItem[] items = new MenuItem[]{
        new MenuItem("JOIN GAME"),
        new MenuItem("NEW GAME"),
        new MenuItem("TEST GAME"),
        new MenuItem("HOW TO PLAY"),
        new MenuItem("EXIT")};

    public void closeServer() {
        if (lobbyServer != null) {
            lobbyServer.closeServer();
        }
    }

    private class TriCircle extends Parent {
        private TriCircle() {
            Shape shape1 = Shape.subtract(new Circle(8), new Circle(2));
            shape1.setFill(textColor);

            Shape shape2 = Shape.subtract(new Circle(8), new Circle(2));
            shape2.setFill(textColor);
            shape2.setTranslateX(5);

            Shape shape3 = Shape.subtract(new Circle(8), new Circle(2));
            shape3.setFill(textColor);
            shape3.setTranslateX(2.5);
            shape3.setTranslateY(-5);

            getChildren().addAll(shape1, shape2, shape3);

            setEffect(new GaussianBlur(2));
        }
    }

    private class MenuItem extends HBox {
        private TriCircle c1 = new TriCircle(), c2 = new TriCircle();
        private Text text;
        private Runnable script;

        private MenuItem(String name) {
            super(15);
            setAlignment(Pos.CENTER);

            text = new Text(name);
            text.setFont(font);
            text.setStyle("-fx-stroke: black; -fx-stroke-width: 2px");
            text.setEffect(new DropShadow(5, 0, 5, Color.BLACK));

            getChildren().addAll(c1, text, c2);
            setActive(false);
            setOnActivate(() -> System.out.println(name + " activated"));

            this.setOnMouseClicked(l -> {
                this.activate();
            });

            this.setOnMouseMoved(l -> {
                int i = 0;
                for(MenuItem m : items) {
                    if(m == this)
                        currentItem = i;
                    else
                        m.setActive(false);
                    i++;
                }
                this.setActive(true);
            });
        }

        private void updateSize() {
            text.setFont(font);
        }

        public void setActive(boolean b) {
            c1.setVisible(b);
            c2.setVisible(b);
            text.setFill(b ? Color.WHITE : textColor);
        }

        public void setOnActivate(Runnable r) {
            script = r;
        }

        public void activate() {
            if (script != null)
                script.run();
        }
    }

    private Stage window;

    public void restart(Stage window) {
//        window.setScene(chooseTeamAndRoleScene());
        if (lobbyServer != null) {
            System.out.println("server lobby restartFromReadyPage");
            lobbyServer.restartFromReadyPage();

            lobbyServer.closeServer();

        }
        if (lobbyClient != null) {
            System.out.println("client lobby restartFromReadyPage");
            lobbyClient.restartFromReadyPage();

//            lobbyClient = newRoot LobbyClient(window, new LobbyClient2LobbyAdaptor() {
//                @Override
//                public void showChoiceTeamAndRoleScene() {
//                    Platform.runLater(() -> window.setScene(chooseTeamAndRoleScene()));
//                }
//            }, this);
        }
    }

    @Override
    public void start(Stage window) {
        this.window = window;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        window.getIcons().add(LOGO_IMAGE);
        window.setResizable(true);
        window.setMinHeight(400);
        window.setMinWidth(600);
        window.setWidth(bounds.getWidth() - 50);
        window.setHeight(bounds.getHeight() - 50);
        window.setX(25);
        window.setY(25);
        window.setTitle("King Slayer");
        window.setFullScreen(true);

//        Canvas bgCanvas = new Canvas();
//        GraphicsContext bgGC = bgCanvas.getGraphicsContext2D();
//        Canvas midCanvas = new Canvas();
//        GraphicsContext midGC = midCanvas.getGraphicsContext2D();


        items[0].setOnActivate(this::connectForm);
        items[1].setOnActivate(this::guiSetNumOfPlayer);
        items[2].setOnActivate(this::testGame);
        items[3].setOnActivate(this::howToPlay);
        items[4].setOnActivate(this::exit);
        menuBox = new VBox(10, items);
        items[0].setActive(true);

        InvalidationListener resize = l -> {
            //  if(window.isMaximized()) {
            //      window.setHeight(bounds.getHeight());
            //      window.setWidth(bounds.getWidth());
            //  }
            bgCanvas.setWidth(window.getWidth());
            bgCanvas.setHeight(window.getHeight());
            midCanvas.setWidth(window.getWidth());
            midCanvas.setHeight(window.getHeight());
            font = Font.font("", FontWeight.BOLD, window.getHeight()/400 * 18);
            for(MenuItem item : items)
                item.updateSize();
        };

        window.heightProperty().addListener(resize);
        window.widthProperty().addListener(resize);
        window.maximizedProperty().addListener(resize);

        Group root = new Group();
        root.getChildren().add(bgCanvas);
        root.getChildren().add(midCanvas);
        root.getChildren().add(menuBox);
        mainMenuScene = new Scene(root);

        mainMenuScene.setCursor(new ImageCursor(CURSOR_IMAGE, 0, 0));

        mainMenuScene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case F11:
                    window.setFullScreen(true);
                    break;
                case UP:
                case W:
                    if (currentItem > 0) {
                        items[currentItem].setActive(false);
                        items[--currentItem].setActive(true);
                    }
                    break;
                case DOWN:
                case S:
                    if (currentItem < menuBox.getChildren().size() - 1) {
                        items[currentItem].setActive(false);
                        items[++currentItem].setActive(true);
                    }
                    break;
                case ENTER:
                case SPACE:
                    items[currentItem].activate();
                    break;
            }
        });

        double[] bgOpac = new double[]{2.0};

        animator = new AnimationTimer() {

            @Override
            public void handle(long now) {
                midGC.clearRect(0, 0, window.getWidth(), window.getHeight());
                midGC.setFill(Color.color(0.6, 0.6, 0.7, 0.4 + (Math.abs(bgOpac[0] % 2 - 1)) * 0.3));
                bgOpac[0] += 0.002;

                midGC.fillRect(0, 0, window.getWidth(), window.getHeight());
                midGC.drawImage(LOGO_TEXT_IMAGE, window.getWidth()/4, 50, window.getWidth()/2, (153/645.0)*window.getWidth()/2);

                bgGC.drawImage(MENU_SPASH_BG_IMAGE, 0, 0, window.getWidth(), window.getHeight());

                menuBox.setTranslateX(window.getWidth() / 2 - menuBox.getWidth() / 2);
                menuBox.setTranslateY(window.getHeight() - menuBox.getHeight() - 100);
            }
        };



        animator.start();
        resize.invalidated(null);
        sleep(100);
        window.setScene(mainMenuScene);
        window.show();
    }

    private void connectForm() {
        Platform.runLater(() -> window.setScene(ipFormScene()));
//        window.setScene(new Scene(ipForm(), 800, 500));
    }

    private Scene ipFormScene() {
        Group newRoot = new Group();
        newRoot.getChildren().add(bgCanvas);
        newRoot.getChildren().add(midCanvas);
        Pane newPane = ipForm();
        newRoot.getChildren().add(newPane);

        Scene ret = new Scene(newRoot);
        ret.setCursor(new ImageCursor(CURSOR_IMAGE, 0, 0));

        setAnimator(newPane);

        return ret;
    }

    public GridPane choiceTeamAndRolePane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(window.getHeight()/2, 100, window.getHeight()/2 + 100, 100));
        grid.setVgap(5);
        grid.setHgap(5);

        teamChoice = new ChoiceBox<>();
        teamChoice.getItems().add(Team.ONE);
        teamChoice.getItems().add(Team.TWO);
        GridPane.setConstraints(teamChoice, 0, 0);
        grid.getChildren().add(teamChoice);

        roleChoice = new ChoiceBox<>();
        roleChoice.getItems().add(Role.KING);
        roleChoice.getItems().add(Role.SLAYER);
        GridPane.setConstraints(roleChoice, 1, 0);
        grid.getChildren().add(roleChoice);

        Button ready = new Button("Ready");
        GridPane.setConstraints(ready, 2, 0);
        grid.getChildren().add(ready);

        ready.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Log.info("client click ready");
                ready();
                //the following would be done in the network part

//                window.setScene(new Scene(choiceTeamAndRoleScene()));
            }

        });

        return grid;
    }

    private GridPane ipForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(window.getHeight()/2, 100, window.getHeight()/2 + 100, 100));
        grid.setVgap(5);
        grid.setHgap(5);

        //Defining the Name text field
        final TextField ip = new TextField();
        ip.setPromptText("Enter ip.");
        ip.setPrefColumnCount(100);
//        ip.getText();
        GridPane.setConstraints(ip, 0, 0);
        grid.getChildren().add(ip);

        Button connect = new Button("Connect");
        GridPane.setConstraints(connect, 1, 0);
        grid.getChildren().add(connect);

        connect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (connected) {
                    return;
                }
                String ipStr = ip.getText();
                try {
                    joinGame(ipStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //TODO: Change here to ping back later
                connected = true;
                //the following would be done in the network part

//                window.setScene(new Scene(choiceTeamAndRoleScene()));
            }

        });

        return grid;
    }

    public Scene inputNumOfPlayersScene() {

//        Canvas bgCanvas = new Canvas();
//        GraphicsContext bgGC = bgCanvas.getGraphicsContext2D();
//        Canvas midCanvas = new Canvas();
//        GraphicsContext midGC = midCanvas.getGraphicsContext2D();

        Group newRoot = new Group();
        newRoot.getChildren().add(bgCanvas);
        newRoot.getChildren().add(midCanvas);
        Pane inputPane = inputNumOfPlayers();
        newRoot.getChildren().add(inputPane);

        Scene ret = new Scene(newRoot);
        ret.setCursor(new ImageCursor(CURSOR_IMAGE, 0, 0));

        setAnimator(inputPane);

        return ret;
    }

    public Scene chooseTeamAndRoleScene() {

        Group newRoot = new Group();
        newRoot.getChildren().add(bgCanvas);
        newRoot.getChildren().add(midCanvas);
        Pane inputPane = choiceTeamAndRolePane();
        newRoot.getChildren().add(inputPane);

        Scene ret = new Scene(newRoot);
        ret.setCursor(new ImageCursor(CURSOR_IMAGE, 0, 0));

        setAnimator(inputPane);

        return ret;
    }

    private void setAnimator(Pane addedPane) {

        InvalidationListener resize = l -> {
            //  if(window.isMaximized()) {
            //      window.setHeight(bounds.getHeight());
            //      window.setWidth(bounds.getWidth());
            //  }
            bgCanvas.setWidth(window.getWidth());
            bgCanvas.setHeight(window.getHeight());
            midCanvas.setWidth(window.getWidth());
            midCanvas.setHeight(window.getHeight());
            font = Font.font("", FontWeight.BOLD, window.getHeight()/400 * 18);
            for(MenuItem item : items)
                item.updateSize();
        };


            window.heightProperty().addListener(resize);
            window.widthProperty().addListener(resize);
            window.maximizedProperty().addListener(resize);


            double[] bgOpac = new double[]{2.0};
            animator = new AnimationTimer() {

            @Override
            public void handle(long now) {
                midGC.clearRect(0, 0, window.getWidth(), window.getHeight());
                midGC.setFill(Color.color(0.6, 0.6, 0.7, 0.4 + (Math.abs(bgOpac[0] % 2 - 1)) * 0.3));
                bgOpac[0] += 0.002;

                midGC.fillRect(0, 0, window.getWidth(), window.getHeight());
                midGC.drawImage(LOGO_TEXT_IMAGE, window.getWidth()/4, 50, window.getWidth()/2, (153/645.0)*window.getWidth()/2);

                bgGC.drawImage(MENU_SPASH_BG_IMAGE, 0, 0, window.getWidth(), window.getHeight());

                addedPane.setTranslateX(window.getWidth() / 2 - addedPane.getWidth() / 2);
                addedPane.setTranslateY(window.getHeight() - addedPane.getHeight() + 200);
            }
        };

        animator.start();
    }

    private GridPane inputNumOfPlayers() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(window.getHeight()/2, 100, window.getHeight()/2 + 100, 100));
        grid.setVgap(5);
        grid.setHgap(5);

        //Defining the Name text field
        final TextField numOfPlayer = new TextField();
        numOfPlayer.setPromptText("Enter Number Of Players.");
        numOfPlayer.setPrefColumnCount(100);

        GridPane.setConstraints(numOfPlayer, 0, 0);
        grid.getChildren().add(numOfPlayer);

        Button set = new Button("set");
        GridPane.setConstraints(set, 1, 0);
        grid.getChildren().add(set);

        set.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lobbyServer.setNumOfPlayers(Integer.parseInt(numOfPlayer.getText()));
                startGame();
                //the following would be done in the network part

//                window.setScene(new Scene(choiceTeamAndRoleScene()));
            }

        });

        return grid;
    }

    private void guiSetNumOfPlayer() {
        lobbyServer = new LobbyServer();
        try {
            lobbyServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lobbyClient = new LobbyClient(window, new LobbyClient2LobbyAdaptor() {
            @Override
            public void showChoiceTeamAndRoleScene() {
                Platform.runLater(() -> window.setScene(chooseTeamAndRoleScene()));
            }
        }, this);

//        window.getScene().getRoot().getChildrenUnmodifiable().remove(0, 1);

        Platform.runLater(() -> window.setScene(inputNumOfPlayersScene()));
//        Platform.runLater(() -> window.setScene(new Scene(inputNumOfPlayers())));
    }

    private void startGame() {

        try {

            lobbyClient.start();
            lobbyClient.connectTo("localhost");
            //TODO: change this to Ping back later
            Thread.sleep(2000); //(connection needs time)
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void ready() {
        //TODO: get from the view later!!!!!!!!!!!
        Team team = teamChoice.getValue();
        Role role = roleChoice.getValue();

        lobbyClient.lobbyClientReady(team, role);
    }

    private void joinGame(String host) throws Exception {
        Log.info("JOIN GAME SELECTED");
        lobbyClient = new LobbyClient(window, new LobbyClient2LobbyAdaptor() {
            @Override
            public void showChoiceTeamAndRoleScene() {
                Platform.runLater(() -> window.setScene(chooseTeamAndRoleScene()));
//                window.setScene(new Scene(choiceTeamAndRoleScene()));
            }
        }, this);
        lobbyClient.start();
        lobbyClient.connectTo(host);
        //TODO: change this to ping back later
        Thread.sleep(2000);
    }

    private void testGame() {
        Log.info("TEST GAME SELECTED");
        try {
            new SingleplayerController(this).start(window);
        } catch (Exception e) {
            e.printStackTrace();
        }
        animator.stop();
    }

    private void howToPlay() {
        Log.info("HOW TO PLAY SELECTED");
    }

    private void exit() {
        Log.info("EXIT SELECTED");
        Platform.exit();
    }

    public static void main(String args[]) {
        Log.set(Log.LEVEL_INFO);
        Application.launch();
    }

}
