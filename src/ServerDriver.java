

public class ServerDriver {
	public static void main(String[] args) {
		System.out.println("Starting server.");
		ServerController sc = new ServerController();
		sc.start();
	}
}
