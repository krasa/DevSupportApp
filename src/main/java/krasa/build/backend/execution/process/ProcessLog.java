package krasa.build.backend.execution.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import krasa.build.backend.dto.Result;
import krasa.core.backend.common.ArrayListDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessLog {
	protected final static Logger log = LoggerFactory.getLogger(SshBuildProcess.class);
	public static final int NUM_ELEMENTS = 2500;
	public static final int BUFFER_MAX_LENGTH = 2500;
	protected final StringBuilder buffer = new StringBuilder();
	protected final ArrayListDeque<String> deque = new ArrayListDeque<String>(NUM_ELEMENTS);
	boolean stop = false;
	int dequeueOffset = 0;

	public String getContent() {
		return getNext(0).getText();
	}

	public void setContent(String content) {
		append(content);
	}

	private synchronized void appendProxy(String str) {
		if (buffer.length() > BUFFER_MAX_LENGTH) {
			// remove first element if there is no room in queue
			if (deque.size() >= NUM_ELEMENTS) {
				deque.removeOldest();
				dequeueOffset++;
			}
			deque.insert(str);
		} else {
			buffer.append(str);
		}
	}

	public void stop() {
		stop = true;
	}

	public boolean isStop() {
		return stop;
	}

	public ProcessLog append(String str) {
		appendProxy(str);
		return this;
	}

	public ProcessLog newLine() {
		appendProxy("\n");
		return this;
	}

	public Result getNext(int position) {
		int resultPosition = buffer.length() + dequeueOffset;
		if (position == resultPosition + deque.size()) {
			return Result.empty(position);
		}
		StringBuilder sb = new StringBuilder();
		if (position < buffer.length()) {
			sb.append(buffer.substring(position, buffer.length()));
		}

		int dequeStart = Math.max(position - buffer.length(), 0);
		if (!deque.isEmpty()) {
			if (dequeueOffset > dequeStart) {
				sb.append("\n...").append(dequeueOffset - Math.max((position - buffer.length()), 0)).append(
						" lines skipped\n");
				dequeStart = 0;
			} else {
				dequeStart = dequeStart - dequeueOffset;
				resultPosition = resultPosition + dequeStart;
			}

			for (int i = dequeStart; i < deque.size(); i++) {
				String s = deque.get(i);
				sb.append(s);
				resultPosition++;
			}
		}

		return new Result(resultPosition, sb.toString());
	}

	public void receiveUntilLineEquals(InputStream inputStream, String until) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while (!isStop() && (line = r.readLine()) != null) {
			append(line).newLine();
			if (until.equals(line)) {
				log.trace("until condition received: " + line);
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

}
