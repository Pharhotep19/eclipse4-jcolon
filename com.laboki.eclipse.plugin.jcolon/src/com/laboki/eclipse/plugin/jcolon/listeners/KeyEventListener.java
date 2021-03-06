package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.BaseListener;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;

public final class KeyEventListener extends BaseListener
	implements
		KeyListener {

	private final Optional<Control> control =
		EditorContext.getControl(EditorContext.getEditor());

	public KeyEventListener() {
		super();
	}

	@Override
	public void
	keyPressed(final KeyEvent arg0) {
		EditorContext.cancelAllJobs();
	}

	@Override
	public void
	keyReleased(final KeyEvent arg0) {
		BaseListener.scheduleErrorChecking();
	}

	@Override
	public void
	add() {
		if (!this.control.isPresent()) return;
		this.control.get().addKeyListener(this);
	}

	@Override
	public void
	remove() {
		if (!this.control.isPresent()) return;
		this.control.get().removeKeyListener(this);
	}
}
