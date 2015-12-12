package server;

import server.packets.Packet05Chat;

public interface ServerListener {
	/**
	 * Called when the server receives a login packet.
	 * 
	 * @param player
	 */
	void serverDidAddPlayer(ServerPlayer player);
	
	/**
	 * Called when a server is already running on the 
	 * specific ip.
	 */
	void serverAlreadyRunning();
	
	/**
	 * Called when a player is removed from the server.
	 * Could be from player disconnect.
	 * 
	 * @param player
	 */
	void serverDidRemovePlayer(ServerPlayer player);
	
	/**
	 * Called when a player sends a message.
	 * 
	 * @param chatPacket
	 */
	void serverDidRecieveChatPacket(Packet05Chat chatPacket);
}
