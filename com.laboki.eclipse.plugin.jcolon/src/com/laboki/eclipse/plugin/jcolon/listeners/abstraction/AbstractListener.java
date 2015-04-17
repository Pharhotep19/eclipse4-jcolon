package com.laboki.eclipse.plugin.jcolon.listeners.abstraction;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.laboki.eclipse.plugin.jcolon.instance.EventBusInstance;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public abstract class AbstractListener extends EventBusInstance
	implements
		IListener {

	private static final Logger LOGGER =
		Logger.getLogger(AbstractListener.class.getName());

	public AbstractListener() {
		super();
	}

	@Override
	public final Instance
	start() {
		this.tryToAdd();
		return super.start();
	}

	private void
	tryToAdd() {
		try {
			this.add();
		}
		catch (final Exception e) {
			AbstractListener.LOGGER.log(Level.WARNING, e.getMessage());
		}
	}

	@Override
	public void
	add() {}

	@Override
	public final Instance
	stop() {
		this.tryToRemove();
		return super.stop();
	}

	private void
	tryToRemove() {
		try {
			this.remove();
		}
		catch (final Exception e) {
			AbstractListener.LOGGER.log(Level.FINEST, e.getMessage());
		}
	}

	@Override
	public void
	remove() {}

	protected final static void
	scheduleErrorChecking() {
		EditorContext.cancelJobsBelongingTo(EditorContext.LISTENER_TASK);
		AbstractListener.scheduleTask();
	}

	private static void
	scheduleTask() {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void
			execute() {
				EditorContext.scheduleErrorChecking();
			}
		}.setName(EditorContext.LISTENER_TASK)
			.setDelay(EditorContext.LONG_DELAY_TIME)
			.start();
	}
}
