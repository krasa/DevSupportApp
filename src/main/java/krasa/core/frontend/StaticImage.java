package krasa.core.frontend;

public enum StaticImage {
	CONTENT_COPY("5_content_copy.png"),
	DELETE("5_content_discard.png"),
	EDIT("5_content_edit.png"),
	BUILD("9-av-play-over-video.png"),

	;
	String path;
	Integer height;

	private StaticImage(String path) {
		this.path = "/img/" + path;
	}

	private StaticImage(String path, Integer height) {
		this.height = height;
		this.path = "/img/" + path;
	}

	public Integer getHeight() {
		return height;
	}

	public String getPath() {
		return path;
	}
}
