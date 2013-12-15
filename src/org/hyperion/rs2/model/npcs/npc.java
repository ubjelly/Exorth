package org.hyperion.rs2.model.npcs;

import org.hyperion.rs2.content.NPCStyle;
import org.hyperion.rs2.content.combat.CombatCheck;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;

/**
 * Default NPC. NOT FINISHED YET!
 * @author phil
 *
 */
public class npc extends NPC {

	/**
	 * Constructor
	 * @param id The NPC id.
	 */
	public npc(int id) {
		super(id);
		// TODO Auto-generated constructor stub
		health = getDefinition().getMaxHealth();
	}
	
	/**
	 * Constructor
	 * @param id The NPC id.
	 * @param loc The spawning location.
	 */
	public npc(int id, Location loc) {
		super(id, loc);
		// TODO Auto-generated constructor stub
		health = getDefinition().getMaxHealth();
	}
	
	/**
	 * Creates the NPC with the specified definition.
	 * @param definition The definition.
	 */
	public npc(int i, Location origin, int range) {
		super(i, origin, range);
		this.health = getDefinition().getMaxHealth();
	}

	@Override
	public int determineNextCombatAttack(Entity Opp) {
		// TODO Auto-generated method stub
		if(getFightStyle() != -1) return getFightStyle();
		byte nextIndex = (byte) (NPCStyle.attacks.get(getId()) != null ? CombatCheck.random(NPCStyle.attacks.get(getId()).getAtts().length-1) : 0);
		setFightIndex(nextIndex);
		setFightStyle(NPCStyle.attacks.get(getId()).getAtts()[nextIndex].getStyle());
		return getFightStyle();
	}

	@Override
	public void sendDeathEffects(Entity Opp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void distributeAttackEffects(Entity Opp, int hit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean withinAllowedZone(Location loc) {
		// TODO Auto-generated method stub
		return this.getOrigin().withinRange(getLocation(), 10 + this.getWalkRange());
	}

	@Override
	public void executeSpawnAffects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activateHealthStageEffects(Entity Opp) {
		// TODO Auto-generated method stub
		
	}

}
