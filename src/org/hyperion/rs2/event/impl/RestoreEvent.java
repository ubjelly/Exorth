package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;

/**
 * Restores skills.
 * @author phil
 *
 */
public class RestoreEvent extends Event {

	/**
	 * Constructor.
	 */
	public RestoreEvent() {
		super(30000);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		for(Player player : World.getWorld().getPlayers()) {
			//Check for dead.
			if(player.isDead()) continue;
			//Normalize levels
			for(int i = 0; i < Skills.SKILL_COUNT; i++)
				player.getSkills().normalizeLevel(i);
			//special energy restore.
			if(player.getSpecialAmount() < 100)
				player.setSpecialAmount((byte) (player.getSpecialAmount() + 10));
		}
	}

}
