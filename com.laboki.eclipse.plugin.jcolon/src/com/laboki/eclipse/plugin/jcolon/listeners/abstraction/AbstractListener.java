package com.laboki.eclipse.plugin.jcolon.listeners.abstraction;

import com.laboki.eclipse.plugin.jcolon.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;
import com.laboki.eclipse.plugin.jcolon.main.EventBus;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public abstract class AbstractListener extends AbstractEventBusInstance implements IListener {

	public AbstractListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public Instance begin() {
		this.tryToAdd();
		return super.begin();
	}

	private void tryToAdd() {
		try {
			this.add();
		} catch (final Exception e) {}
	}

	@Override
	public void add() {}

	@Override
	public Instance end() {
		this.tryToRemove();
		return super.end();
	}

	private void tryToRemove() {
		try {
			this.remove();
		} catch (final Exception e) {}
	}

	@Override
	public void remove() {}

	protected void scheduleErrorChecking() {
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
