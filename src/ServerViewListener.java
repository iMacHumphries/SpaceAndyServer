
public interface ServerViewListener {
	void sendMessageToClients(String message);
	
	void sendKillToClients(String username);
	
	void sendKickToClients(String username, String reason);
	
	void sendStopServer();
}
