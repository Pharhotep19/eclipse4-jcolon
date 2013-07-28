package com.laboki.eclipse.plugin.jcolon.listeners.abstraction;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.laboki.eclipse.plugin.jcolon.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;
import com.laboki.eclipse.plugin.jcolon.main.EventBus;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public abstract class AbstractListener extends AbstractEventBusInstance implements IListener {

	private static final Logger LOGGER = Logger.getLogger(AbstractListener.class.getName());

	public AbstractListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public final Instance begin() {
		this.tryToAdd();
		return super.begin();
	}

	private void tryToAdd() {
		try {
			this.add();
		} catch (final Exception e) {
			AbstractListener.LOGGER.log(Level.WARNING, e.getMessage());
		}
	}

	@Override
	public void add() {}

	@Override
	public final Instance end() {
		this.tryToRemove();
		return super.end();
	}

	private void tryToRemove() {
		try {
			this.remove();
		} catch (final Exception e) {
			AbstractListener.LOGGER.log(Level.WARNING, e.getMessage());
		}
	}

	@Override
	public void remove() {}

	protected final void scheduleErrorChecking() {
		EditorContext.cancelJobsBelongingTo(EditorContext.LISTENER_TASK);
		this.scheduleTask();
	}

	private void scheduleTask() {
		new Task(EditorContext.LISTENER_TASK, EditorContext.LONG_DELAY_TIME) {

			@Override
			public boolean shouldSchedule() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void execute() {
				EditorContext.scheduleErrorChecking(AbstractListener.this.eventBus);
			}
		}.begin();
	}
}
