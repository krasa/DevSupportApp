package krasa.svn.frontend.pages.config;

import java.io.Serializable;

public class User implements Serializable {

	private String hostName;
	private String userName;

	public User(String host, String name) {
		hostName = host;
		userName = name;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
