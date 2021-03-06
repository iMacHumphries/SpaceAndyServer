package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import constants.Constants;
import server.packets.*;

/**
 * Server.java - SpaceAndy server
 * 
 * @author Ben & Andy
 * @version 25-NOV-2015
 */
public class Server extends Thread {
	private static final int MAX_BYTES = 1024;

	private DatagramSocket socket;                    // Server socket
	private ArrayList<ServerPlayer> connectedPlayers; // List of server players online
	
	private ServerListener delegate; // Delegate work to others

	/**
	 * Constructor
	 */
	public Server(ServerListener listener) {
		this.delegate = listener;
		connectedPlayers = new ArrayList<>();
		try {
			this.socket = new DatagramSocket(Constants.PORT_NUMBER);
		} catch (SocketException e) {
			delegate.serverAlreadyRunning();
		}
	}

	/**
	 * Server run
	 */
	public void run() {
		while (true) {
			Lock lock = new ReentrantLock();
			lock.lock();
			byte[] data = new byte[MAX_BYTES];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			parsePacket(packet);
		}
	}

	public void speak(String msg) {
		Packet05Chat chatPack = new Packet05Chat("SERVER", msg);
		delegate.serverDidRecieveChatPacket(chatPack);
		this.sendDataToAllClients(chatPack.getData());
	}

	public void kickPlayer(String name, String reason) {
		Packet06Kick packet = new Packet06Kick(name, reason);
		byte[] data = packet.getData();
		parsePacket(new DatagramPacket(data, data.length));
	}

	/**
	 * Parses the packets received from the run
	 * 
	 * @param packet
	 */
	private void parsePacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		InetAddress ip = packet.getAddress();
		int port = packet.getPort();
		String message = new String(packet.getData());
		String id = message.substring(0, 2);

		PacketType type = Packet.findPacket(id);
		switch (type) {
		case INVALID:
			System.out.println("got invalid packet");
			break;
		case LOGIN:
			Packet01Login loginPacket = new Packet01Login(data);
			ServerPlayer player = new ServerPlayer(loginPacket.getUsername(),
					ip, port);
			addConnection(player, loginPacket);
			delegate.serverDidAddPlayer(player);
			speak("Welcome " + player.getUsername());
			break;
		case DISCONNECT:
			this.removeConnection(new Packet00Disconnect(data));
			break;
		case MOVE:
			this.handleMovePacket(new Packet02Move(data));
			break;
		case SHOOT:
			this.sendDataToAllClients(data);
			break;
		case KILL:
			Packet04Kill kp = new Packet04Kill(data);
			this.sendDataToAllClients(kp.getData());
			break;
		case CHAT:
			Packet05Chat chatPacket = new Packet05Chat(data);
			delegate.serverDidRecieveChatPacket(chatPacket);
			this.sendDataToAllClients(data);
			break;
		case KICK:
			Packet06Kick kickPacket = new Packet06Kick(data);
			ServerPlayer playerToKick = serverPlayerWithName(kickPacket
					.getUsername());
			if (playerToKick != null) {
				removeConnection(new Packet00Disconnect(
						kickPacket.getUsername()));
				sendData(kickPacket.getData(), playerToKick.getIp(),
						playerToKick.getPort());
			}
			break;
		case STOP:
			System.out.println("Should we allow client to shutdown server? hmmm");
			break;
		}
	}

	public void handleMovePacket(Packet02Move movePacket) {
		for (ServerPlayer player : this.connectedPlayers) {
			if (!player.getUsername().equals(movePacket.getUsername())) {
				this.sendData(movePacket.getData(), player.getIp(),
						player.getPort());
			} else {
				player.setX(movePacket.getX());
				player.setY(movePacket.getY());
				player.setRotZ(movePacket.getRotZ());
			}
		}
	}

	/**
	 * Sends data to given ip and port.
	 * 
	 * @param data
	 * @param ip
	 * @param port
	 */
	public void sendData(byte[] data, InetAddress ip, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends data to all clients in server.
	 * 
	 * @param data
	 */
	public void sendDataToAllClients(byte[] data) {
		for (ServerPlayer player : connectedPlayers) {
			sendData(data, player.getIp(), player.getPort());
		}
	}

	/**
	 * Adds a new player to the server Sends the new player login packets for
	 * every online player. Sends the players already logged on the new players
	 * loginpacket.
	 * 
	 * @param player
	 * @param loginPacket
	 */
	public void addConnection(ServerPlayer player, Packet01Login loginPacket) {
		boolean alreadyConnected = false;

		player.setX(loginPacket.getX());
		player.setY(loginPacket.getY());
		player.setRotZ(loginPacket.getRotZ());

		// Go through all players
		for (ServerPlayer p : connectedPlayers) {
			// Check if they have connected before
			if (p.getUsername().equalsIgnoreCase(player.getUsername())) {
				alreadyConnected = true;
				// Check if there is data for that player
				if (p.getIp() == null)
					p.setIp(player.getIp());
				if (p.getPort() == -1)
					p.setPort(player.getPort());
			} else {
				// Haven't connected.
				// Sending the new player login to every connected player
				sendData(loginPacket.getData(), p.getIp(), p.getPort());

				// Sending every already connected player to the new player
				Packet01Login lp = new Packet01Login(p.getUsername(), p.getX(),
						p.getY(), p.getRotZ());
				sendData(lp.getData(), player.getIp(), player.getPort());
			}
		} // End of for loop.

		if (!alreadyConnected) {
			connectedPlayers.add(player);
		}
	}

	public void removeConnection(Packet00Disconnect disconnectPacket) {
		// Search for the disconnecting player
		ServerPlayer disconnectingPlayer = null;
		for (ServerPlayer p : connectedPlayers)
			if (p.getUsername()
					.equalsIgnoreCase(disconnectPacket.getUsername()))
				disconnectingPlayer = p;

		// Remove the player from connected players
		if (disconnectingPlayer != null) {
			connectedPlayers.remove(disconnectingPlayer);
			delegate.serverDidRemovePlayer(disconnectingPlayer);
		}

		// Send the disconnect to every client
		this.sendDataToAllClients(disconnectPacket.getData());
	}

	public ArrayList<ServerPlayer> getPlayers() {
		return connectedPlayers;
	}

	public ServerPlayer serverPlayerWithName(String name) {
		ServerPlayer result = null;
		for (ServerPlayer player : connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(name)) {
				result = player;
				break;
			}
		}
		return result;
	}

	public boolean isPlayerOnline(String name) {
		boolean result = false;
		for (ServerPlayer player : connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(name)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public void shutDown() {
		this.sendDataToAllClients(new Packet07Stop().getData());
	}

}
