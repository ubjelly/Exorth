package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.NPC;

/**
 * Respawns NPC.
 * @author phil
 *
 */
public class RespawnEvent extends Event {

	/**
	 * Constructor.
	 * @param n The npc being respawned.
	 */
	public RespawnEvent(NPC n) {
		super(n.getDefinition().getSpawnTimer() * 1000);
		// TODO Auto-generated constructor stub
		this.n = n;
	}
	
	/**
	 * Local stored variable.
	 */
	NPC n;

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

}
