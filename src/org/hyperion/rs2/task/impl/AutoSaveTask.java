package org.hyperion.rs2.task.impl;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.task.Task;

/**
 * Saves a player's account.
 * 
 * @author Stephen
 * 
 */
public class AutoSaveTask implements Task {

	@Override
	public void execute(GameEngine context) {
		context.submitWork(new Runnable() {
			public void run() {
				for (final Player player : World.getWorld().getPlayers()) {
					World.getWorld().getWorldLoader().savePlayer(player);
					//System.out.println("Game saved for " + player + ".");
				}
			}
		});
	}

}