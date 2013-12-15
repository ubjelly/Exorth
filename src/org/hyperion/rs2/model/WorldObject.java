package org.hyperion.rs2.model;

public class WorldObject {

	/**
	 * The object id.
	 */
	public int objectId;

	/**
	 * The x coordinate of the object.
	 */
	public int objectX;
	
	/**
	 * The y coordinate of the object.
	 */
	public int objectY;
	
	/**
	 * The height level of the object.
	 */
	public int objectHeight;
	
	/**
	 * The direction on which the object faces.
	 */
	public int objectDirection;
	
	/**
	 * The type of the object.
	 */
	public int objectType;
	
	/**
	 * If the object will be replaced by another, the replacement id.
	 */
	public int replacementId;
	
	/**
	 * The amount of ticks until the object reverts back to its original.
	 */
	public int ticks;
	
	/**
	 * Creates a new world object.
	 * @param objectId The object id.
	 * @param objectX The object's x coordinate.
	 * @param objectY The object's y coordinate.
	 */
	public WorldObject(final int objectId, final int objectX, final int objectY) {
		this.objectId = objectId;
		this.objectX = objectX;
		this.objectY = objectY;
		this.objectHeight = 0;
		this.objectDirection = 0;
		this.objectType = 10;
		this.replacementId = -1;
		this.ticks = 100;
		WorldObjectManager.addObject(this);
	}
	
	/**
	 * Creates a new world object.
	 * @param objectId The object id.
	 * @param objectX The object's x coordinate.
	 * @param objectY The object's y coordinate.
	 * @param replacementId The replacement id.
	 * @param ticks The amount of ticks until the object reverts back to its original.
	 */
	public WorldObject(final int objectId, final int objectX, final int objectY, final int replacementId, final int ticks) {
		this.objectId = objectId;
		this.objectX = objectX;
		this.objectY = objectY;
		this.objectHeight = 0;
		this.objectDirection = 0;
		this.objectType = 10;
		this.replacementId = replacementId;
		this.ticks = ticks;
		WorldObjectManager.addObject(this);
	}
	
	/**
	 * Creates a new world object.
	 * @param objectId The object id.
	 * @param objectX The object's x coordinate.
	 * @param objectY The object's y coordinate.
	 * @param objectHeight The height of the object.
	 * @param objectDirection The direction the object will face.
	 * @param objectType The type of the object.
	 * @param replacementId The replacement id.
	 * @param ticks The amount of ticks until the object reverts back to its original.
	 */
	public WorldObject(final int objectId, final int objectX, final int objectY, final int objectHeight, final int objectDirection, final int objectType, final int replacementId, final int ticks) {
		this.objectId = objectId;
		this.objectX = objectX;
		this.objectY = objectY;
		this.objectHeight = objectHeight;
		this.objectDirection = objectDirection;
		this.objectType = objectType;
		this.replacementId = replacementId;
		this.ticks = ticks;
		WorldObjectManager.addObject(this);
	}
}