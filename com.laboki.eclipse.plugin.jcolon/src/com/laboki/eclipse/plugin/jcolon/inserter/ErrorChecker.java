package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.DelayedTask;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.CheckForSemiColonErrorsEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.LocateSemiColonErrorEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

final class ErrorChecker implements Instance, VerifyListener {

	private EventBus eventBus;
	private StyledText buffer = EditorContext.getBuffer(EditorContext.getEditor());

	public ErrorChecker(final EventBus eventBus) {
		this.eventBus = eventBus;
		this.eventBus.register(this);
	}

	@Override
	public Instance begin() {
		this.checkError();
		this.buffer.addVerifyListener(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		this.buffer.removeVerifyListener(this);
		this.nullifyFields();
		return this;
	}

	@Override
	public void verifyText(final VerifyEvent arg0) {
		this.checkError();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void checkForSemiColonErrors(@SuppressWarnings("unused") final CheckForSemiColonErrorsEvent event) {
		this.checkError();
	}

	private void checkError() {
		EditorContext.asyncExec(new Task("") {

			@Override
			public void execute() {
				ErrorChecker.cancelJobs();
				ErrorChecker.this.findSemiColonError();
			}
		});
	}

	private static void cancelJobs() {
		EditorContext.cancelJobsBelongingTo(EditorContext.TASK_FAMILY_NAME);
	}

	private void findSemiColonError() {
		EditorContext.asyncExec(new DelayedTask(EditorContext.TASK_FAMILY_NAME, EditorContext.DELAY_TIME_IN_MILLISECONDS) {

			@Override
			public void execute() {
				ErrorChecker.this.postEvent();
			}
		});
	}

	private void postEvent() {
		this.eventBus.post(new SyncFilesEvent());
		this.eventBus.post(new LocateSemiColonErrorEvent());
	}

	private void nullifyFields() {
		this.buffer = null;
		this.eventBus = null;
	}
}
