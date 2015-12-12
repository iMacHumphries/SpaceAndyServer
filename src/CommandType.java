
public enum CommandType {
	INVALID(""),
	HELP ("help"),
	STOP ("stop"),
	KILL ("kill"),
	SAY("say"),
	KICK("kick");
	
	private String cmd;
	private CommandType(String cmd) {
		this.cmd = cmd;
	}
	
	public static CommandType findCommandType(String cmd) {
		for (CommandType t : values()) {
			if (t.getCmd().equalsIgnoreCase(cmd))
				return t;
		}
		return INVALID;
	}
	
	public String getCmd() {
		return this.cmd;
	}
	
	public static String stringValues() {
		String result = "";
		for (CommandType t : values())
			if (t != INVALID)
				result += "/" + t.getCmd()+ ", ";
		return result;
	}
}
