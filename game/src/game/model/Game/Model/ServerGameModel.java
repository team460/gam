package game.model.Game.Model;

import com.esotericsoftware.minlog.Log;
import game.message.*;
import game.model.Game.Map.ServerMapGenerator;
import game.model.Game.WorldObject.Entity.Entity;
import game.model.Game.WorldObject.Entity.Player;
import game.model.Game.WorldObject.Team;

import java.util.ArrayList;
import java.util.Collection;

import static Util.Const.*;

public class ServerGameModel extends GameModel {

    public ServerGameModel() {
        super(new ServerMapGenerator(GRID_X_SIZE, GRID_Y_SIZE));
    }

    private Collection<? extends Model> clients = null;

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

    public void init(Collection<? extends Model> clients) {
        this.clients = clients;

        // Send map to client
        for(Model client : clients)
            for (int i = 0; i < this.getMapWidth(); i++)
                for (int j = 0; j < this.getMapWidth(); j++) {
                    client.processMessage(new SetTileMessage(i, j, this.getTile(i, j)));
                }

        ArrayList<Player> players = new ArrayList<>();
        for (Entity entity : this.getAllEntities()) {
            if(entity instanceof Player) {
                entity.setTeam(Team.valueOf(((players.size()/2) % 2) + 1));
                players.add((Player) entity);
            }
        }

        //Send all enttities to clients
        for(Entity entity : this.getAllEntities())
            clients.forEach(client -> client.processMessage(new SetEntityMessage(entity)));

        int i = 0;
        // Send player to client
        for(Model model : clients) model.processMessage(new SetPlayerMessage(players.get(i++)));
    }

    @Override
    public String toString() {
        return "Server Game Model";
    }

    private boolean running = false;

    public void start() {
        Log.info("Starting Server Model");
        if (running) throw new RuntimeException("Cannot start server model when already running");
        running = true;
        (new Thread(this::run, this.toString() + " Update Thread")).start();
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    private void run() {
        while (running) {
            long start = System.nanoTime();
            this.update();
            //want it independent of how long update take, so use the following instead
            //of thread.sleep()...
            long delta = System.nanoTime()- start;
            if (UPDATE_LOOP_TIME_NANOS > delta)
                try {
                    Thread.yield();
                    Thread.sleep((UPDATE_LOOP_TIME_NANOS - delta)/ 1000000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }
}
