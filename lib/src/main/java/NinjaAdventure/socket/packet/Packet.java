package NinjaAdventure.socket.packet;

import NinjaAdventure.socket.InGameClient;
import NinjaAdventure.socket.InGameServer;

public abstract class Packet {

    public static enum PacketTypes {
        INVALID(-1), JOIN_GAME(00), DISCONNECT(01), MOVE(02), UPDATE_LIFE(03);

        private int packetId;

        private PacketTypes(int packetId) {
            this.packetId = packetId;
        }

        public int getId() {
            return packetId;
        }
    }

    public byte packetId;

    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }

    public abstract void writeData(InGameClient client);

    public abstract void writeData(InGameServer server);

    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public abstract byte[] getData();

    public static PacketTypes lookupPacket(String packetId) {
        try {
            return lookupPacket(Integer.parseInt(packetId));
        } catch (NumberFormatException e) {
            return PacketTypes.INVALID;
        }
    }

    public static PacketTypes lookupPacket(int id) {
        for (PacketTypes p : PacketTypes.values()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return PacketTypes.INVALID;
    }
}
