package game.model.game.model;

import com.esotericsoftware.minlog.Log;
import game.message.Message;
import game.message.toServer.RequestEntityRequest;
import game.model.game.grid.GridCell;
import game.model.game.map.ClientMapGenerator;
import game.model.game.map.Tile;
import game.model.game.model.gameState.GameState;
import game.model.game.model.gameState.Loading;
import game.model.game.model.gameState.Running;
import game.model.game.model.team.Role;
import game.model.game.model.team.Team;
import game.model.game.model.team.TeamResourceData;
import game.model.game.model.team.TeamRoleEntityMap;
import game.model.game.model.worldObject.entity.Entity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.Util;

import java.util.Comparator;
import java.util.function.Consumer;

import static game.model.game.model.worldObject.entity.Entity.EntityProperty.DRAW_STRAT;
import static util.Const.DEBUG_DRAW;
import static util.Util.toDrawCoords;

public class ClientGameModel extends GameModel {

    private TeamResourceData resourceData = new TeamResourceData();

    Team winningTeam = null;
    Team losingTeam = null;


    public ClientGameModel(Model server) {
        super(new ClientMapGenerator());
        this.server = server;
    }

    private long localPlayer;

    public Entity getLocalPlayer() {
        return this.getEntity(localPlayer);
    }

    public void setResourceData(TeamResourceData data) {
        resourceData = data;
    }

    public TeamResourceData getResourceData() {
        return resourceData;
    }

    public void setLocalPlayer(long localPlayer) {
        Log.info("Set local player");
        this.localPlayer = localPlayer;
    }

    private Model server;

    @Override
    public void processMessage(Message m) {
        if(server == null)
            throw new RuntimeException("Cannot receive message before init()");
        if (m.sendToClient())
            this.queueMessage(m);
        if (m.sendToServer())
            server.processMessage(m);
    }

    public void init(Team team, Role role, TeamRoleEntityMap map, Tile[][] gameMap) {
        this.setLocalPlayer(map.getEntity(team, role));
        for(int x = 0; x < getMapWidth(); x++)
            for(int y = 0; y < getMapHeight(); y++)
                this.getCell(x,y).setTile(gameMap[x][y], this);
        state = Running.SINGLETON;
    }

    @Override
    public long nanoTime() {
        return server.nanoTime();
    }

    @Override
    public String toString() {
        return "Client game model";
    }

    public void requestEntityFromServer(long id) {
        server.processMessage(new RequestEntityRequest(id));
    }

    @Override
    public void execute(Consumer<ServerGameModel> serverAction, Consumer<ClientGameModel> clientAction) {
        clientAction.accept(this);
    }


    /**
     * returns approximately all the entities inside of the box centered at x,y with width, height
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public void drawForeground(GraphicsContext gc, double x, double y, double w, double h) {
        this.streamCells().filter(cell -> cell.isVisable(this.getLocalPlayer().getTeam())).flatMap(GridCell::streamContents).filter(entity -> entity.has(DRAW_STRAT)).sorted(Comparator.comparingDouble(Entity::getDrawZ)).forEach(a -> a.draw(gc, this));
        this.streamCells().filter(cell -> !cell.isVisable(this.getLocalPlayer().getTeam())).forEach(cell -> {
            if(cell.isExplored(this.getLocalPlayer().getTeam()))
                gc.setFill(Color.color(0,0,0,0.3));
            else
                gc.setFill(Color.BLACK);
            gc.fillRect(toDrawCoords(cell.getTopLeftX()), toDrawCoords(cell.getTopLeftY()), toDrawCoords(1), toDrawCoords(1));
        });

        if(DEBUG_DRAW)
            this.streamCells().flatMap(GridCell::streamContents).forEach(a -> a.getHitbox().draw(gc, a));
    }

    public void writeBackground(WritableImage image, boolean b) {
        this.streamCells().forEach(cell -> cell.draw(image.getPixelWriter(), this, b));
    }

    public void changeWinningTeam(Team team) {
        if (winningTeam == getLocalPlayer().getTeam()) return; //already won
        winningTeam = team;
    }

    public void changeLosingTeam(Team team) {
        if (losingTeam == getLocalPlayer().getTeam()) return; //already lost
        losingTeam = team;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

    public Team getLosingTeam() {
        return losingTeam;
    }

    private GameState state = Loading.SINGLETON;

    public GameState getState() {
        return state;
    }
}
