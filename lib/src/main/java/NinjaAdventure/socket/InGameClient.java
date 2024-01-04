package NinjaAdventure.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import NinjaAdventure.game.src.entity.PlayerMP;
import NinjaAdventure.game.src.main.GamePanel;
import NinjaAdventure.socket.packet.Packet;
import NinjaAdventure.socket.packet.Packet.PacketTypes;
import NinjaAdventure.socket.packet.Packet00JoinGame;
import NinjaAdventure.socket.packet.Packet02Move;

public class InGameClient extends Thread {
	private InetAddress ipAddress;
    private DatagramSocket socket;
    private GamePanel game;

    public InGameClient(GamePanel game, String ipAddress) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
        Packet packet = null;
        switch (type) {
        default:
        case INVALID:
            break;
        case JOIN_GAME:
            packet = new Packet00JoinGame(data);
//            System.out.println("[" + address.getHostAddress() + ":" + port + "] "
//                    + ((Packet00JoinGame) packet).getUsername() + " has joined the game...");
//            PlayerMP player = new PlayerMP(game, null, ((Packet00JoinGame) packet).getUsername(), address, port);
//            game.addEntity(player);
            handleLogin((Packet00JoinGame) packet, address, port);
            break;
        case DISCONNECT:
//            packet = new Packet01Disconnect(data);
//            System.out.println("[" + address.getHostAddress() + ":" + port + "] "
//                    + ((Packet01Disconnect) packet).getUsername() + " has left the world...");
//            game.level.removePlayerMP(((Packet01Disconnect) packet).getUsername());
            break;
//        case MOVE:
//            packet = new Packet02Move(data);
//            handleMove((Packet02Move) packet);
        }
    }

    public void sendData(byte[] data) {
    	DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(Packet00JoinGame packet, InetAddress address, int port) {
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()
                + " has joined the game...");
        PlayerMP player = new PlayerMP(game, packet.getUsername(), address, port, packet.getX(), packet.getY());
        game.addEntity(player);
    }

//    private void handleMove(Packet02Move packet) {
//        this.game.movePlayer(packet.getUsername(), packet.getX(), packet.getY());
//    }
}
