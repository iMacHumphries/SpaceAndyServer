import server.Server;
import server.ServerListener;
import server.ServerPlayer;
import server.packets.Packet;

public class ServerController implements ServerListener{
	private ServerView view;
	
	private Server server;

	public ServerController() {
		view = new ServerView();
		server = new Server(this);
	
	}
	
	public void start() {
		server.start();
	}
	
	public void sendPacketToAllClients( Packet packet) {
		server.sendDataToAllClients(packet.getData());
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

}
