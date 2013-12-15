package org.hyperion.rs2.action.impl;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;

/**
 * <p>A destruction action is a resource-destroying action, which includes, but
 * is not limited to, prayer.</p>
 * 
 * <p>This class implements code related to all destruction-type skills, such as
 * dealing with the action itself, looping, checking requirements and
 * destroying the destruction item.</p>
 * 
 * <p>The individual bone burying class implements things specific
 * to these individual skills such as random events.</p>
 * @author Stephen
 *
 */
public abstract class DestructionAction extends Action {
	
	/**
	 * The location.
	 */
	private Location location;
	
	/**
	 * Creates the harvesting action for the specified player.
	 * @param player The player to create the action for.
	 */
	public DestructionAction(Player player) {
		super(player, 0);
	}
	
	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.ALWAYS;
	}
	
	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}
	
	/**
	 * Called when the action is initialized.
	 */
	public abstract void init();
	
	/**
	 * Gets the item being destroyed.
	 * @return The destruction item.
	 */
	public abstract Item getDestructionItem();
	
	/**
	 * Gets the harvest delay.
	 * @return The delay between consecutive destructions.
	 */
	public abstract long getDestructionDelay();
	
	/**
	 * Gets the number of cycles.
	 * @return The number of cycles.
	 */
	public abstract int getCycles();
	
	/**
	 * Gets the experience.
	 * @return The experience.
	 */
	public abstract double getExperience();
	
	/**
	 * Gets the skill.
	 * @return The skill.
	 */
	public abstract int getSkill();
	
	/**
	 * Gets the animation.
	 * @return The animation.
	 */
	public abstract Animation getAnimation();
	
	/**
	 * Gets the item's id.
	 * @return The item id.
	 */
	public abstract int getItemId();
	
	/**
	 * The total number of cycles.
	 */
	private int totalCycles;
	
	/**
	 * The number of remaining cycles.
	 */
	private int cycles;
	
	/**
	 * Grants the player his or her experience.
	 * @param player The player object.
	 * @param reward The item reward object.
	 */
	private void giveExperience(Player player) {
		player.getInventory().remove(getDestructionItem());
		player.getSkills().addExperience(getSkill(), getExperience());
	}

	@Override
	public void execute() {
		final Player player = getPlayer();
		if(this.getDelay() == 0) {
			this.setDelay(getDestructionDelay());
			init();
			if(this.isRunning()) {
				player.playAnimation(getAnimation());
				player.face(location);
			}
			this.cycles = getCycles();
			this.totalCycles = cycles;
		} else {
			cycles--;
			Item item = getDestructionItem();
			if(player.getInventory().contains(item.getId())) {
				if(totalCycles == 1) {
					giveExperience(player);
				}
			} else {
				stop();
				return;
			}
			if(cycles == 0) {
				stop();
			} else {
				player.playAnimation(getAnimation());
			}
		}
	}
}
