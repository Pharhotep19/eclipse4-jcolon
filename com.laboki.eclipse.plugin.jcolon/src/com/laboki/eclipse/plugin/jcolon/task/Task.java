package com.laboki.eclipse.plugin.jcolon.task;

public abstract class Task extends BaseTask {

	public Task() {}

	@Override
	protected TaskJob
	newTaskJob() {
		return new TaskJob() {

			@Override
			protected void
			runTask() {
				Task.this.execute();
			}
		};
	}

	@Override
	public abstract void
	execute();
}
