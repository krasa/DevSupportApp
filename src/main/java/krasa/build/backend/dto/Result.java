package krasa.build.backend.dto;

import java.io.Serializable;

public class Result implements Serializable {
	private final int length;
	private final String text;

	public Result() {
		length = 0;
		text = "";
	}

	public Result(int length, String text) {
		this.length = length;
		this.text = text;
	}

	public int getLength() {
		return length;
	}

	public String getText() {
		return text;
	}

	public boolean isNotEmpty() {
		return text.length() > 0;
	}

	public static Result empty(int position) {
		return new Result(position, "");
	}
}
