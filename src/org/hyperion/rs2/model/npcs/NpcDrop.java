package org.hyperion.rs2.model.npcs;

/** test
 * Handles anything to do with Npcs dropping items.
 * @author Stephen
 */
public class NpcDrop {

	/**
	 * The item id.
	 */
	private int itemId;
	
	/**
	 * The item amount.
	 */
	private int itemAmount;
	
	/**
	 * The chance to drop.
	 */
	private double dropChance;
	
	public NpcDrop(int itemId, int itemAmount, double dropChance) {
		this.itemId = itemId;
		this.itemAmount = itemAmount;
		this.dropChance = dropChance;
	}
	
	/**
	 * Gets the item id.
	 * @return the item id.
	 */
	public int getItemId() {
		return itemId;
	}
	
	/**
	 * Gets the item amount.
	 * @return the item amount.
	 */
	public int getItemAmount() {
		return itemAmount;
	}
	
	/**
	 * Gets the drop chance.
	 * @return the drop chance.
	 */
	public double getDropChance() {
		return dropChance;
	}
}
