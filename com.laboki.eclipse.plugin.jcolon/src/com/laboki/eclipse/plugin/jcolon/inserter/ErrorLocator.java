package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.DelayedTask;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.inserter.events.AnnotationModelChangedEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SemiColonErrorLocationEvent;

final class ErrorLocator implements Instance {

	private EventBus eventBus;
	private Problem problem = new Problem();
	private IEditorPart editor = EditorContext.getEditor();

	public ErrorLocator(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void annotationModelChanged(@SuppressWarnings("unused") final AnnotationModelChangedEvent event) {
		EditorContext.asyncExec(new DelayedTask("", 1000) {

			@Override
			public void execute() {
				ErrorLocator.this.findErrorLocation();
			}
		});
	}

	private void findErrorLocation() {
		if (this.hasMissingSemiColonError()) this.postEvent(this.problem.location());
	}

	private boolean hasMissingSemiColonError() {
		return this.hasJDTErrors() && this.problem.isMissingSemiColonError();
	}

	private boolean hasJDTErrors() {
		return EditorContext.hasJDTErrors(this.editor);
	}

	private void postEvent(final int location) {
		this.eventBus.post(new SemiColonErrorLocationEvent(location));
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		this.problem.end();
		this.nullifyFields();
		return this;
	}

	private void nullifyFields() {
		this.eventBus = null;
		this.problem = null;
		this.editor = null;
	}
}
