package de.jumpingpxl.jumpingaddon.util;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Task {

	private final Runnable runnable;
	private ScheduledExecutorService executorService;

	private Task(Runnable runnable) {
		this.runnable = runnable;
	}

	public static Task of(Runnable runnable) {
		return new Task(runnable);
	}

	public void run() {
		runnable.run();
	}

	public void runLater(long delay, TimeUnit timeUnit) {
		setExecutorService();
		executorService.schedule(this::run, delay, timeUnit);
	}

	public void runRepeat(long initialDelay, long delay, TimeUnit timeUnit) {
		setExecutorService();
		executorService.scheduleAtFixedRate(this::run, initialDelay, delay, timeUnit);
	}

	public void runRepeat(long delay, TimeUnit timeUnit) {
		runRepeat(0, delay, timeUnit);
	}

	public void runAsync() {
		CompletableFuture.runAsync(runnable);
	}

	public void runLaterAsync(long delay, TimeUnit timeUnit) {
		setExecutorService();
		executorService.schedule(this::runAsync, delay, timeUnit);
	}

	public void runRepeatAsync(long initialDelay, long delay, TimeUnit timeUnit) {
		setExecutorService();
		executorService.scheduleAtFixedRate(this::runAsync, initialDelay, delay, timeUnit);
	}

	public void runRepeatAsync(long delay, TimeUnit timeUnit) {
		runRepeatAsync(0, delay, timeUnit);
	}

	public boolean cancel() {
		if (Objects.isNull(executorService)) {
			return false;
		}

		executorService.shutdown();
		return true;
	}

	private void setExecutorService() {
		if (Objects.nonNull(executorService)) {
			throw new UnsupportedOperationException("Task is already running");
		}

		executorService = Executors.newSingleThreadScheduledExecutor();
	}
}
