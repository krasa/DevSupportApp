package krasa.build.backend.config;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.core.task.AsyncTaskExecutor;

public class HandlingExecutor implements AsyncTaskExecutor {

	private AsyncTaskExecutor executor;

	public HandlingExecutor(AsyncTaskExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void execute(Runnable task) {
		executor.execute(task);
	}

	@Override
	public void execute(Runnable task, long startTimeout) {
		executor.execute(createWrappedRunnable(task), startTimeout);

	}

	@Override
	public Future<?> submit(Runnable task) {
		return executor.submit(createWrappedRunnable(task));
	}

	@Override
	public <T> Future<T> submit(final Callable<T> task) {
		return executor.submit(createCallable(task));
	}

	private <T> Callable<T> createCallable(final Callable<T> task) {
		return new Callable<T>() {
			@Override
			public T call() throws Exception {
				try {
					return task.call();
				} catch (Exception e) {
					handle(e);
					throw e;
				}
			}
		};
	}

	private Runnable createWrappedRunnable(final Runnable task) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					task.run();
				} catch (Exception e) {
					handle(e);
				}
			}
		};
	}

	private void handle(Exception e) {
		System.out.println("CAUGHT!");
	}
}
