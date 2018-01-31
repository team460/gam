package game.network;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryonet.EndPoint;
import game.message.*;
import game.message.playerMoveMessage.*;
import game.model.Game.Map.Tile;
import game.model.Game.WorldObject.Entity.*;
import game.model.Game.WorldObject.Shape.*;
import game.model.Game.WorldObject.Shape.Shape;
import game.model.Game.WorldObject.Team;

import java.util.ArrayList;

public class NetworkCommon {
    static public int port = 54555;
    static public final int REMOTEMODEL = 1;
    // This registers serializes files
    public static void register (EndPoint endPoint) {
        KyroRegister(endPoint.getKryo());
    }

    public static void KyroRegister(Kryo kryo) {
        kryo.register(Message.class);
        kryo.register(PlayerStopDown.class);
        kryo.register(PlayerStopUp.class);
        kryo.register(PlayerStopLeft.class);
        kryo.register(PlayerStopRight.class);
        kryo.register(ActionMessage.class);
//        kryo.register(MoveMessage.class);
        kryo.register(RemoveEntityMessage.class);
        kryo.register(SetEntityMessage.class);
        kryo.register(SetTileMessage.class);
        kryo.register(ToClientMessage.class);
        kryo.register(ToServerMessage.class);
        kryo.register(SetPlayerMessage.class);

        kryo.register(Player.class);
        kryo.register(Blocker.class);
        kryo.register(Entity.class);
        kryo.register(Tree.class);
        kryo.register(Shape.GridCellReference.class);

        kryo.register(Stone.class);
        kryo.register(Metal.class);

        kryo.register(Shape.class);
        kryo.register(CellShape.class);
        kryo.register(CircleShape.class);
        kryo.register(CompositeShape.class);
        kryo.register(RectShape.class);

        kryo.register(Team.class);

        DefaultSerializers.EnumSerializer tileSerializer = new DefaultSerializers.EnumSerializer(Tile.class);
        kryo.register(Tile.class, tileSerializer);



        kryo.register(ClientMakeModelMsg.class);
        kryo.register(ClientReadyMsg.class);

        kryo.register(RemoteConnection.GameConnection.class);
        kryo.register(ClientStartModelMsg.class);
        kryo.register(ArrayList.class);
        kryo.register(SyncClockMsg.class);

    }

    public static class ClientMakeModelMsg {
        public ClientMakeModelMsg() {
        }
    }

    public static class ClientReadyMsg {
        public ClientReadyMsg() {}
    }

    public static class ClientStartModelMsg {
        public ClientStartModelMsg() {}
    }

    public static class SyncClockMsg {
        private long serverTime;
        public SyncClockMsg() {}
        public SyncClockMsg(long serverNanoTime) {
            serverTime = serverNanoTime;
        }
        public long getServerTime() {
            return serverTime;
        }
    }

//    public static class BatchMsg {
//        ArrayList<Message> msgs;
//        public BatchMsg() {}
//        public BatchMsg(ArrayList<Message> messages) {
//            msgs = messages;
//        }
//        public ArrayList<Message> getMsgs() {
//            return msgs;
//        }
//    }

}

