package org.hyperion.rs2.model;

import java.util.ArrayList;

import org.hyperion.rs2.event.impl.WorldObjectRemovalEvent;

public class WorldObjectManager {
	
	/**
	 * A list of all the world objects.
	 */
	public static ArrayList<WorldObject> objects = new ArrayList<WorldObject>();

	/**
	 * A list of all the objects that are to be removed.
	 */
	public final static ArrayList<WorldObject> remove = new ArrayList<WorldObject>();
	
	/**
	 * Gets an object.
	 * @param x The object's x coordinate.
	 * @param y The object's y coordinate.
	 * @param z The object's height level.
	 * @return The object.
	 */
	public static WorldObject getObject(final int x, final int y, final int z) {
		for (final WorldObject o : objects) {
			if (o.objectX == x && o.objectY == y && o.objectHeight == z) {
				return o;
			}
		}
		return null;
	}
	
	/**
	 * Adds an object.
	 * @param o The object instance.
	 */
	public static void addObject(final WorldObject o) {
		if (getObject(o.objectX, o.objectY, o.objectHeight) == null) {
			objects.add(o);
			for (final Player player : World.getWorld().getPlayers()) {
				if (player != null) {
					final Player p = (Player) player;
					p.getLocation();
					if (p.getLocation().isWithinDistance(Location.create(o.objectX, o.objectY, o.objectHeight))) {
						p.getActionSender().sendWorldObjectCreation(o, false);
						World.getWorld().submit(new WorldObjectRemovalEvent());
					}
				}
			}
		}
	}
	
	/**
	 * Removes an object.
	 * @param o The object instance.
	 */
	public static void removeObject(WorldObject o) {
		for (final Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				final Player p = (Player) player;
				p.getActionSender().sendWorldObjectRemoval(o);
			}
		}
	}
	
	/**
	 * Updates an object.
	 * @param o The object instance.
	 */
	public static void updateWorldObject(WorldObject o) {
		for (final Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				final Player p = (Player) player;
				p.getActionSender().sendWorldObjectCreation(o, true);
			}
		}
	}
	
	/**
	 * Checks to see if the object exists by x and y coordinates.
	 * @param o The object instance.
	 * @return If the object exists or not.
	 */
	public static boolean objectExists(WorldObject o) {
		for (final WorldObject object : objects) {
			if (o.objectX == object.objectX && o.objectY == object.objectY) {
				return true;
			}
		}
		return false;
	}
}