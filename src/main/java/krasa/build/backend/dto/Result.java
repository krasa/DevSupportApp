package krasa.build.backend.dto;

import java.io.Serializable;

public class Result implements Serializable {
	private final int offset;
	private final String text;

	public Result() {
		offset = 0;
		text = "";
	}

	public Result(int offset, String text) {
		this.offset = offset;
		this.text = text;
	}

	public Result(String s) {
		this(s.length(), s);
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

	public static Result empty(int position) {
		return new Result(position, "");
	}

	@Override
	public String toString() {
		return "Result{" + "length=" + offset + ", text='" + text + '\'' + '}';
	}
}
