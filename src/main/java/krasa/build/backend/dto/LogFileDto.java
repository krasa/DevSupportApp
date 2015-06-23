package krasa.build.backend.dto;

import java.io.Serializable;

public class LogFileDto implements Serializable {

	private final int offset;
	private final String text;

	public LogFileDto(int offset, String text) {
		this.offset = offset;
		this.text = text;
	}

	public int getOffset() {
		return offset;
	}

	public String getText() {
		return text;
	}

	public boolean isNotEmpty() {
		return text.length() > 0;
	}

	public static LogFileDto empty(int position) {
		return new LogFileDto(position, "");
	}

	@Override
	public String toString() {
		return "Result{" + "length=" + offset + ", text='" + text + '\'' + '}';
	}
}
