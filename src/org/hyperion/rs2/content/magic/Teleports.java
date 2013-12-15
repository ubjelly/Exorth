package org.hyperion.rs2.content.magic;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;

public class Teleports {

	/**
	 * Represents modern spellbook teleports.
	 * @author Stephen
	 */
	enum Modern {
		
		/**
		 * Lubridge teleport.
		 */
		LUMBRIDGE(1167, 3222, 3218, 0),
		
		/**
		 * Varrock teleport.
		 */
		VARROCK(1164, 3222, 3218, 0),
		
		/**
		 * Falador teleport.
		 */
		FALADOR(1170, 3222, 3218, 0),
		
		/**
		 * Camelot teleport.
		 */
		CAMELOT(1174, 3222, 3218, 0),
		
		/**
		 * Ardougne teleport.
		 */
		ARDOUGNE(1540, 3222, 3218, 0),
		
		/**
		 * Watchtower teleport.
		 */
		WATCHTOWER(1541, 3222, 3218, 0);
		
		/**
		 * The action button.
		 */
		private short actionButton;
		
		/**
		 * The x coordinate.
		 */
		private short xPos;
		
		/**
		 * The y coordinate.
		 */
		private short yPos;
		
		/**
		 * The height level.
		 */
		private short height;
		
		/**
		 * A map of action buttons to teleports.
		 */
		private static Map<Short, Modern> modernTele = new HashMap<Short, Modern>();
		
		/**
		 * Gets a teleport by the action button.
		 * @param actionButton The action button.
		 * @return The teleport, or <code>null</code> if the action button is not a teleport.
		 */
		public static Modern forId(int actionButton) {
			return modernTele.get(actionButton);
		}
		
		/**
		 * Populates the modernTele map.
		 */
		static {
			for(Modern modern : Modern.values()) {
				modernTele.put(modern.actionButton, modern);
			}
		}
		
		/**
		 * Creates the teleport.
		 * @param actionButtion The action button.
		 * @param xPos The x position.
		 * @param yPos The y position.
		 * @param height The height level.
		 */
		private Modern(int actionButton, int xPos, int yPos, int height) {
			this.actionButton = (short) actionButton;
			this.xPos = (short) xPos;
			this.yPos = (byte) yPos;
			this.height = (short) height;
		}
		
		/**
		 * Gets the x position.
		 * @return The x position.
		 */
		public int getXPos() {
			return xPos;
		}
		
		/**
		 * Gets the y position.
		 * @return The y position.
		 */
		public int getYPos() {
			return yPos;
		}
		
		/**
		 * Gets the height level.
		 * @return The the height level.
		 */
		public int getHeight() {
			return height;
		}
	}
	
	/**
	 * The teleport graphic
	 */
	private int teleportGraphic = 308;
	
	/**
	 * The teleport animation
	 */
	private int teleportAnimation = 714;
	
	/**
	 * Starts the teleport action.
	 * @param player The player teleporting.
	 * @param actionButton The action button.
	 */
	public static void startTeleport(Player player, int actionButton) {
		Modern modernTeleport = Modern.forId(actionButton);
		Location loc = Location.create(modernTeleport.getXPos(), modernTeleport.getYPos(), modernTeleport.getHeight());

		player.setTeleportTarget(loc);
	}

}
