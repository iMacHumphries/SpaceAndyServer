import server.*;
import server.packets.*;


public class ServerController implements ServerListener, ServerViewListener {
	private ServerView view;
	
	private Server server;

	public ServerController() {
		server = new Server(this);
		view = new ServerView(this);
	}
	
	public void start() {
		server.start();
	}
	
	@Override
	public void serverDidAddPlayer(ServerPlayer player) {
		view.appendStringToLog(player.getUsername() + " has logged in from " + player.getIp());
		view.displayPlayers(server.getPlayers());
	}

	@Override
	public void serverAlreadyRunning() {
		view.appendStringToLog("[ERROR] Server is alreay running on this ip.");
		
	}

	@Override
	public void serverDidRemovePlayer(ServerPlayer player) {
		view.appendStringToLog(player.getUsername() + " has disconnected.");
		view.displayPlayers(server.getPlayers());
	}

	@Override
	public void serverDidRecieveChatPacket(Packet05Chat chatPacket) {
		view.appendStringToLog("[" + chatPacket.getUsername() + "] " + chatPacket.getMessage());
	}

	/*
	 * @see ServerViewListener#sendMessageToClients(java.lang.String)
	 */
	
	@Override
	public void sendMessageToClients(String message) {
		server.speak(message);
	}

	@Override
	public void sendKillToClients(String username) {
		if (server.isPlayerOnline(username)) {
			view.appendStringToLog("Killing " + username + " ...");
			server.sendDataToAllClients(new Packet04Kill(username).getData());
		} else {
			view.appendStringToLog(username + " is not online.");
		}
	}

	@Override
	public void sendKickToClients(String username) {
		if (server.isPlayerOnline(username)) {
			view.appendStringToLog("Kicking " + username + " ...");
			server.kickPlayer(username, "no reason");
		} else {
			view.appendStringToLog(username + " is not online.");
		}
	}
}
