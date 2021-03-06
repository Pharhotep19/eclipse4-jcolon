package com.laboki.eclipse.plugin.jcolon.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.jcolon.events.SemiColonErrorLocationEvent;
import com.laboki.eclipse.plugin.jcolon.instance.EventBusInstance;
import com.laboki.eclipse.plugin.jcolon.task.AsyncTask;

final class Inserter extends EventBusInstance {

	protected static final Logger LOGGER =
		Logger.getLogger(Inserter.class.getName());
	private static final String SEMICOLON = ";";
	protected final Problem problem = new Problem();
	protected final Optional<IEditorPart> editor = EditorContext.getEditor();
	protected final Optional<IDocument> document =
		EditorContext.getDocument(this.editor);
	protected boolean completionAssistantIsActive;

	@Subscribe
	public void
	eventHandler(final SemiColonErrorLocationEvent event) {
		new AsyncTask() {

			@Override
			public boolean
			shouldSchedule() {
				if (Inserter.this.completionAssistantIsActive) return false;
				return true;
			}

			@Override
			public void
			execute() {
				this.insertSemiColon(event.getLocation());
			}

			private void
			insertSemiColon(final int location) {
				try {
					this.tryToInsertSemiColon(location);
				}
				catch (final Exception e) {
					Inserter.LOGGER.log(Level.WARNING, e.getMessage(), e);
				}
			}

			private void
			tryToInsertSemiColon(final int location) throws Exception {
				if (!Inserter.this.document.isPresent()) return;
				if (this.cannotInsertSemiColon(location)) return;
				Inserter.this.document.get().replace(location, 0, Inserter.SEMICOLON);
			}

			private boolean
			cannotInsertSemiColon(final int location) throws Exception {
				return this.semiColonIsAlreadyInserted(location)
					|| this.locationErrorMismatch(location)
					|| EditorContext.isInEditMode(Inserter.this.editor);
			}

			private boolean
			semiColonIsAlreadyInserted(final int location) throws Exception {
				if (this.isEndOfDocument(location)) return false;
				if (!Inserter.this.document.isPresent()) return false;
				return String.valueOf(Inserter.this.document.get().getChar(location))
					.equals(Inserter.SEMICOLON);
			}

			private boolean
			isEndOfDocument(final int location) {
				if (!Inserter.this.document.isPresent()) return false;
				return Inserter.this.document.get().getLength() == location;
			}

			private boolean
			locationErrorMismatch(final int location) {
				try {
					return location != Inserter.this.problem.location();
				}
				catch (final Exception e) {
					return false;
				}
			}
		}.setRule(EditorContext.ERROR_CHECKER_RULE)
			.setFamily(EditorContext.ERROR_CHECKER_FAMILY)
			.setDelay(125)
			.start();
	}

	@Subscribe
	public void
	eventHandler(final AssistSessionStartedEvent event) {
		this.completionAssistantIsActive = true;
	}

	@Subscribe
	public void
	eventHandler(final AssistSessionEndedEvent event) {
		this.completionAssistantIsActive = false;
	}
}
