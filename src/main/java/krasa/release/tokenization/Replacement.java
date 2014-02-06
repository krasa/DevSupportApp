package krasa.release.tokenization;

public class Replacement {

	private String token;
	private String value;

	public Replacement(String token, String value) {
		this.token = token;
		this.value = value;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
