package game.model.game.model;

import com.esotericsoftware.minlog.Log;
import game.message.*;
import game.message.toClient.*;
import game.model.game.map.ServerMapGenerator;
import game.model.game.model.team.Role;
import game.model.game.model.team.Team;
import game.model.game.model.team.TeamResourceData;
import game.model.game.model.team.TeamRoleEntityMap;
import game.model.game.model.worldObject.entity.Entity;
import javafx.util.Pair;
import lobby.PlayerInfo;
import util.Const;

import java.util.*;
import java.util.function.Consumer;

import static util.Const.*;

public class ServerGameModel extends GameModel {

    public ServerGameModel() {
        super(new ServerMapGenerator(GRID_X_SIZE, GRID_Y_SIZE));
        tmpMark = new Random().nextInt();
    }

    private Collection<? extends Model> clients = null;

//    private Map<? extends Model, Pair<Team, Role>> clientToTeamRoleMap;

    private Map<? extends Model, PlayerInfo> clientToPlayerInfo;


    private int counter = 0; // GARBAGE

    Thread updateThread;

    int tmpMark;

    private Map<Team, TeamResourceData> teamData = new HashMap<>();

    public TeamRoleEntityMap teamRoleEntityMap = new TeamRoleEntityMap(NUM_TEAMS, NUM_ROLES);

    public Collection<? extends Model> getClients() {
        return clients;
    }

    public boolean changeResource(Team team, TeamResourceData.Resource r, int num) {
        if (teamData.get(team).getResource(r) + num >= 0 || num >= 0) {
            teamData.get(team).setResource(r, teamData.get(team).getResource(r) + num);
            return true;
        }
        return false;
    }

    @Override
    public void processMessage(Message m) {
        if(clients == null)
            throw new RuntimeException("Cannot receive message before init()");
        if (m.sendToServer())
            this.queueMessage(m);
        if (m.sendToClient())
            clients.forEach(model -> model.processMessage(m));
    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }

    public void init(Collection<? extends Model> clients, Map<? extends Model, PlayerInfo> clientToPlayerInfoMap) {

        this.clients = clients;
        this.clientToPlayerInfo = clientToPlayerInfoMap;
//        this.clientToTeamRoleMap = new HashMap<>();

        // Send teamRoleEntityMap to client
        for (Model client : clients)
            for (int i = 0; i < this.getMapWidth(); i++)
                for (int j = 0; j < this.getMapWidth(); j++) {
                    client.processMessage(new SetTileCommand(i, j, this.getTile(i, j)));
                }

        ArrayList<Entity> players = new ArrayList<>();
        for (Entity entity : this.getAllEntities()) {
            if(entity.team != Team.NEUTRAL) { //TODO this is TEMPORARY
                players.add(entity);
                teamRoleEntityMap.setEntity(entity.team, entity.role, entity.id); // Only for players
            }
        }

        // Send all entities to clients
        for(Entity entity : this.getAllEntities())
            clients.forEach(client -> client.processMessage(new SetEntityCommand(entity)));

        teamData.put(Team.ONE, new TeamResourceData());
        teamData.put(Team.TWO, new TeamResourceData());

        int i = 0;
        // Send player to client
        for(Model model : clients) {
            model.processMessage(new UpdateResourceCommand(teamData.get(players.get(i).team)));
            i++;
        }

        // TODO @tian set each client to the role/team the want
        clients.forEach(client -> {
            client.processMessage(new InitGameCommand(clientToPlayerInfo.get(client).getTeam(),
                    clientToPlayerInfo.get(client).getRole(), teamRoleEntityMap));
        });

    }

    @Override
    public String toString() {
        return "Server game model " + tmpMark;
    }

    private boolean running = false;

    public void start() {
        Log.info("Starting Server model");
        if (running) throw new RuntimeException("Cannot start server model when already running");
        running = true;
        updateThread = new Thread(this::run, this.toString() + " Update Thread");
        updateThread.start();
    }

    public void stop() {
        running = false;
        updateThread.stop();
        clientToPlayerInfo = null;
        teamData = null;
        teamRoleEntityMap = null;
        clients = null;
        updateThread = null;
        System.out.println("old server model stop");
    }

    public void teamWin(Team winTeam) {
        for (Model clientModel : clients) {
            clientModel.processMessage(new TeamWinCommand(winTeam));
        }
        stop();
    }

    public boolean isRunning() {
        return running;
    }

    private void run() {
        boolean[] doAi = {false};

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doAi[0] = true;
            }
        }, 1000, Const.AI_LOOP_UPDATE_TIME_MILI);

        while (running) {
        //    long start = System.nanoTime();
            this.update();
            if(doAi[0]) {
                this.updateAI(this);
                doAi[0] = false;
            }
//            System.err.println("server game model running " + toString());
            //want it independent of how long update take, so use the following instead
            //of thread.sleep()...
        //    long delta = System.nanoTime()- start;
        //    if (UPDATE_LOOP_TIME_NANOS > delta)
         //       try {
                    Thread.yield();
              //      Thread.sleep((UPDATE_LOOP_TIME_NANOS - delta)/ 1000000L);
             //   } catch (InterruptedException e) {
             //       e.printStackTrace();
             //   }

            counter++;

            if (counter > 500) {
                int resourcesRed = 0;
                for (Entity e : this.getAllEntities()) {
                    if (e.team == Team.ONE && e.role == Role.NEUTRAL) {
                        resourcesRed++;
                    }
                }
//                System.out.println("Red: " + resourcesRed);
                changeResource(Team.ONE, TeamResourceData.Resource.WOOD, resourcesRed);

                int resourcesBlue = 0;
                for (Entity e : this.getAllEntities()) {
                    if (e.team == Team.TWO && e.role == Role.NEUTRAL) {
                        resourcesBlue++;
                    }
                }
//                System.out.println("Blue: " + resourcesBlue);
                changeResource(Team.TWO, TeamResourceData.Resource.WOOD, resourcesBlue);

                counter = 0;
            }

            for(Model model : clients) {
                model.processMessage(new UpdateResourceCommand(teamData.get(clientToTeamRoleMap.get(model).getKey()))); //TEMPORARY GARBAGE
            }

        }

        timer.cancel();
    }

    public void makeEntity(Entity e) {
        this.setEntity(e);
        clients.forEach(client -> client.processMessage(new SetEntityCommand(e)));
    }

    @Override
    public void execute(Consumer<ServerGameModel> serverAction, Consumer<ClientGameModel> clientAction) {
        serverAction.accept(this);
    }

    public void removeByID(long entityID) {
        super.removeByID(entityID);
        clients.forEach(client -> client.processMessage(new RemoveEntityCommand(entityID)));
    }

}
