package krasa.build.backend.execution.ssh;

public class SCPInfo {
	private String username;
	private String IP;
	private String password;

	public SCPInfo() {
	}

	public SCPInfo(String username, String password, String IP) {
		this.username = username;
		this.password = password;
		this.IP = IP;
	}

	public String getUsername() {
		return username;
	}

	public void setIP(String IP) {
		this.IP = IP;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIP() {
		return IP;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
