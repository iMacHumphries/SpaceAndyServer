package server;

import java.net.InetAddress;

/**
 * ServerPlayer.java - Models a player on the server.
 * 
 * @author Ben & Andy
 * @verison 25-NOV-2015
 */
public class ServerPlayer {
	private String username;
	private InetAddress ip;
	private int port;
	
	private int x;
	private int y;
	private int rotZ;

	/**
	 * Constructor
	 * 
	 * @param username
	 * @param ip
	 * @param port
	 */
	public ServerPlayer(String username, InetAddress ip, int port) {
		this.username = username;
		this.ip = ip;
		this.port = port;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRotZ() {
		return rotZ;
	}

	public void setRotZ(int rotZ) {
		this.rotZ = rotZ;
	}
	
}
