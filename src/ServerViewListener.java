
public interface ServerViewListener {
	void sendMessageToClients(String message);
	
	void sendKillToClients(String username);
	
	void sendKickToClients(String username);
}
