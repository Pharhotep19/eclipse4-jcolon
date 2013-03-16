package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

@ToString
public final class Factory implements Runnable {

	private final IPartService partService;
	private final PartListener partListener = new PartListener();
	@Getter private final List<IEditorPart> editorParts = new ArrayList<>();

	public Factory(final IPartService partService) {
		this.partService = partService;
		this.partService.addPartListener(this.partListener);
	}

	@Override
	public void run() {
		EditorContext.instance();
		this.enableAutomaticInserterFor(this.partService.getActivePart());
	}

	public void enableAutomaticInserterFor(final IWorkbenchPart part) {
		if (this.isInvalidPart(part)) return;
		if (!EditorContext.isAJavaEditor((IEditorPart) part)) return;
		this.editorParts.add((IEditorPart) part);
		EditorContext.asyncExec(new SemiColonInserter());
	}

	private boolean isInvalidPart(final IWorkbenchPart part) {
		return !this.isValidPart(part);
	}

	private boolean isValidPart(final IWorkbenchPart part) {
		if (part == null) return false;
		if (this.getEditorParts().contains(part)) return false;
		if (part instanceof IEditorPart) return true;
		return false;
	}

	private final class PartListener implements IPartListener {

		public PartListener() {}

		@Override
		public void partActivated(final IWorkbenchPart part) {
			Factory.this.enableAutomaticInserterFor(part);
		}

		@Override
		public void partClosed(final IWorkbenchPart part) {
			if (Factory.this.getEditorParts().contains(part)) Factory.this.getEditorParts().remove(part);
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {}

		@Override
		public void partDeactivated(final IWorkbenchPart part) {}

		@Override
		public void partOpened(final IWorkbenchPart part) {}
	}
}
