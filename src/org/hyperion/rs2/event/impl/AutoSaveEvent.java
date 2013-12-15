package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.task.impl.AutoSaveTask;

/**
 * Autosaving - mainly used for hi-scores.
 * @author Stephen
 */
public class AutoSaveEvent extends Event {

	/**
	 * The delay in milliseconds between save. Currently set at 5 minutes. 1
	 * second = 1,000 milliseconds.
	 */
	public static final int SAVE_CYCLE_TIME = 300000;

	/**
	 * Creates the auto save event to run every 5 minutes.
	 */
	public AutoSaveEvent() {
		super(SAVE_CYCLE_TIME);
	}

	@Override
	public void execute() {
		World.getWorld().submit(new AutoSaveTask());
	}
}