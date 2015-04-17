package com.laboki.eclipse.plugin.jcolon.main;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public final class EventBus {

	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	protected final AsyncEventBus bus = new AsyncEventBus(EventBus.EXECUTOR);

	public EventBus() {}

	public void
	register(final Object object) {
		this.bus.register(object);
	}

	public void
	unregister(final Object object) {
		this.bus.unregister(object);
	}

	public void
	post(final Object object) {
		new Task() {

			@Override
			public void
			execute() {
				EventBus.this.bus.post(object);
			}
		}.start();
	}
}
