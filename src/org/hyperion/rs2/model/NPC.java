package org.hyperion.rs2.model;

import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.task.impl.NPCTickTask;

/**
 * <p>Represents a non-player character in the in-game world.</p>
 * @author Graham Edgecombe
 *
 */
public abstract class NPC extends Entity {
	
	/**
	 * The Id of the NPC.
	 */
	private short id;
	
	/**
	 * The spawning point of NPC.
	 */
	private Location origin;
	
	/**
	 * Walking Range of NPCs.
	 */
	private byte walkRange = 2;
	
	/**
	 * Creates the NPC with the specified definition.
	 * @param definition The definition.
	 */
	public NPC(int i) {
		super();
		this.id = (short)i;
		this.health = getDefinition().getMaxHealth();
		this.origin = Entity.DEFAULT_LOCATION;
		this.setLocation(origin);
	}

	
	/**
	 * Creates the NPC with the specified definition.
	 * @param definition The definition.
	 */
	public NPC(int i, Location origin) {
		super();
		this.id = (short)i;
		this.health = getDefinition().getMaxHealth();
		this.origin = origin;
		this.setLocation(origin);
	}
	
	/**
	 * Creates the NPC with the specified definition.
	 * @param definition The definition.
	 */
	public NPC(int i, Location origin, int range) {
		super();
		this.id = (short)i;
		this.health = getDefinition().getMaxHealth();
		this.origin = origin;
		this.walkRange = (byte) range;
		this.setLocation(origin);
	}
	
	/**
	 * Health.
	 */
	protected int health;
	
	/**
	 * If player is being shown.
	 */
	private short invisibleTimer;
	
	/**
	 * Creates the task for the player. Reduces memory usage.
	 */
	private NPCTickTask tickTask = new NPCTickTask(this);
	
	/**
	 * 
	 */
	private Entity master = null;
	
	/**
	 * Gets the NPC definition.
	 * @return The NPC definition.
	 */
	public NPCDefinition getDefinition() {
		return Definitions.forID((int)id);
	}
	
	public short getId() {
		return id;
	}

	@Override
	public void addToRegion(Region region) {
		region.addNpc(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex();
	}

	@Override
	public int getHealth() {
		// TODO Auto-generated method stub
		return health;
	}

	@Override
	public void applyHealthChange(int amount, boolean limit) {
		// TODO Auto-generated method stub
		health += amount;
	}
	
	@Override
	public int getSize() {
		return getDefinition().getSize();
	}
	
	/**
	 * Gets the offset of the NPC based on size.
	 * @return
	 */
	public int offset() {
		int s = ((getSize() - 1) / 2);
		return s < 1 ? 1 : s;
	}
	
	/**
	 * Determines if in allowed area.
	 * @param loc Location.
	 * @return Terminates combat if in area.
	 */
	public abstract boolean withinAllowedZone(Location loc);
	
	/**
	 * Executes spawn Effects when spawned.
	 */
	public abstract void executeSpawnAffects();
	
	/**
	 * Sends the DeathEffects of the NPC.
	 * @param Opp The opponent.
	 */
	public abstract void sendDeathEffects(Entity Opp);
	
	/**
	 * Sends the Attack Effects of a specific attack.
	 * @param Opp The Opponent.
	 * @param hit The damage inflicted.
	 */
	public abstract void distributeAttackEffects(Entity Opp, int hit);
	
	/**
	 * Activats stages when the health of a NPC is low.
	 * @param Opp The Opponent of the Entity.
	 */
	public abstract void activateHealthStageEffects(Entity Opp);

	/**
	 * @param isInvisible the isInvisible to set
	 */
	public void setInvisible(int timer) {
		this.invisibleTimer = (short) timer;
	}

	/**
	 * @return the isInvisible
	 */
	public boolean isInvisible() {
		return invisibleTimer > 0;
	}
	
	/**
	 * Gets the respawn time.
	 * @return
	 */
	public int getRespawnTime() {
		return invisibleTimer;
	}
	
	/**
	 * Lowers timer.
	 */
	public void lowerInvisibleTimer() {
		if(invisibleTimer == 1)
			setDead(false);
		this.invisibleTimer--;
	}

	/**
	 * @param tickTask the tickTask to set
	 */
	public void setTickTask(NPCTickTask tickTask) {
		this.tickTask = tickTask;
	}

	/**
	 * @return the tickTask
	 */
	public NPCTickTask getTickTask() {
		return tickTask;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Location origin) {
		this.origin = origin;
	}

	/**
	 * @return the origin
	 */
	public Location getOrigin() {
		return origin;
	}

	/**
	 * @param walkRange the walkRange to set
	 */
	public void setWalkRange(byte walkRange) {
		this.walkRange = walkRange;
	}

	/**
	 * @return the walkRange
	 */
	public byte getWalkRange() {
		return walkRange;
	}

	/**
	 * @param master the master to set
	 */
	public void setMaster(Entity master) {
		this.master = master;
	}

	/**
	 * @return the master
	 */
	public Entity getMaster() {
		return master;
	}
	
	/**
	 * Returns whether the entity has a master.
	 * @return
	 */
	public boolean isSummoned() {
		return master != null;
	}
	

}
