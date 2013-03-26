package krasa.build.backend.execution.process;

import java.io.File;

import krasa.build.backend.execution.ProcessLog;
import krasa.build.backend.execution.ProcessStatus;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("FileTailProcess")
public class FileTailProcess implements Process {

	boolean alive = false;
	protected final Tailer tailer;

	public FileTailProcess(final ProcessLog listener, final String pathname) {
		tailer = new Tailer(new File(pathname), new TailerListener() {

			@Override
			public void init(Tailer tailer) {
			}

			@Override
			public void fileNotFound() {
			}

			@Override
			public void fileRotated() {
			}

			@Override
			public void handle(String line) {
				listener.append(line).append("\n");
			}

			@Override
			public void handle(Exception ex) {
			}
		});

	}

	@Override
	public void stop() {
		tailer.stop();
		alive = false;
	}

	@Override
	public void run() {
		alive = true;
		tailer.run();
	}

	@Override
	public ProcessStatus getStatus() {
		return ProcessStatus.alive(alive);
	}
}
