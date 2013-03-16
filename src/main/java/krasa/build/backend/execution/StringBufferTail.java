package krasa.build.backend.execution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import krasa.build.backend.dto.Result;
import krasa.build.backend.execution.process.SshBuildProcess;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringBufferTail implements Appendable {
	protected final static Logger log = LoggerFactory.getLogger(SshBuildProcess.class);

	protected final StringBuffer buffer = new StringBuffer();
	boolean stop = false;

	public void stop() {
		stop = true;
	}

	public boolean isStop() {
		return stop;
	}

	public StringBufferTail append(Throwable obj) {
		String stackTrace = ExceptionUtils.getStackTrace(obj);
		buffer.append(stackTrace);
		return this;
	}

	public StringBufferTail append(Object obj) {
		buffer.append(obj);
		return this;
	}

	public StringBufferTail append(String str) {
		buffer.append(str);
		return this;
	}

	public StringBufferTail newLine() {
		buffer.append("\n");
		return this;
	}

	public StringBufferTail append(StringBufferTail sb) {
		buffer.append(sb);
		return this;
	}

	@Override
	public StringBufferTail append(CharSequence s) {
		buffer.append(s);
		return this;
	}

	@Override
	public StringBufferTail append(CharSequence s, int start, int end) {
		buffer.append(s, start, end);
		return this;
	}

	public StringBufferTail append(char[] str) {
		buffer.append(str);
		return this;
	}

	public StringBufferTail append(char[] str, int offset, int len) {
		buffer.append(str, offset, len);
		return this;
	}

	public StringBufferTail append(boolean b) {
		buffer.append(b);
		return this;
	}

	@Override
	public StringBufferTail append(char c) {
		buffer.append(c);
		return this;
	}

	public StringBufferTail append(int i) {
		buffer.append(i);
		return this;
	}

	public StringBufferTail appendCodePoint(int codePoint) {
		buffer.appendCodePoint(codePoint);
		return this;
	}

	public StringBufferTail append(long lng) {
		buffer.append(lng);
		return this;
	}

	public StringBufferTail append(float f) {
		buffer.append(f);
		return this;
	}

	public StringBufferTail append(double d) {
		buffer.append(d);
		return this;
	}

	public int length() {
		return buffer.length();
	}

	public Result getNext(int position) {
		int length = buffer.length();
		if (length == position) {
			return Result.empty(position);
		}
		String substring = buffer.substring(position, length);
		return new Result(length, substring);
	}

	public void receiveUntilLineEquals(InputStream inputStream, String until) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while (!isStop() && (line = r.readLine()) != null) {
			append(line).newLine();
			if (until.equals(line)) {
				log.debug("until condition received: " + line);
				return;
			}
		}
		log.debug("receiving done");

	}

	public Thread printingThread(final PrintStream out) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				Result next = new Result();
				while (!isStop()) {
					next = getNext(next.getLength());
					if (next.isNotEmpty()) {
						out.print(next.getText());
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public Thread receivingThread(final InputStream inputStream) throws IOException {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					receiveUntilLineEquals(inputStream, "logout");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public String toString() {
		return buffer.toString();
	}
}
