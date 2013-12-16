package org.hyperion.rs2.content.magic;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.event.*;

public class Teleports {

	/**
	 * Represents modern spellbook teleports.
	 * @author Stephen
	 */
	public enum Modern {
		
		/**
		 * Lubridge teleport.
		 */
		LUMBRIDGE(1167, 3222, 3218, 0),
		
		/**
		 * Varrock teleport.
		 */
		VARROCK(1164, 3210, 3424, 0),
		
		/**
		 * Falador teleport.
		 */
		FALADOR(1170, 2964, 3378, 0),
		
		/**
		 * Camelot teleport.
		 */
		CAMELOT(1174, 2757, 3477, 0),
		
		/**
		 * Ardougne teleport.
		 */
		ARDOUGNE(1540, 2662, 3305, 0),
		
		/**
		 * Watchtower teleport.
		 * No clue what coords are, goes to lumby.
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
			return modernTele.get((short)actionButton);
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
			this.yPos = (short) yPos;
			this.height = (short) height;
		}
		
		/**
		 * Gets the x position.
		 * @return The x position.
		 */
		public int getActionButton() {
			return actionButton;
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
	private static int teleportGraphic = 308;
	
	/**
	 * The teleport animation
	 */
	private static int teleportAnimation = 714;
	
	/**
	 * Starts the teleport action.
	 * @param player The player teleporting.
	 * @param actionButton The action button.
	 */
	public static void startTeleport(final Player player, int actionButton) {
		Modern modernTeleport = Modern.forId(actionButton);
		final Location loc = Location.create(modernTeleport.getXPos(), modernTeleport.getYPos(), modernTeleport.getHeight());

		player.playAnimation(Animation.create(teleportAnimation, 5));
		World.getWorld().submit(new Event(650) {
			public void execute() {
				player.playGraphics(Graphic.create(teleportGraphic, 100 << 16));
				stop();
			}
		});
		World.getWorld().submit(new Event(1500) {
			public void execute() {
				player.setTeleportTarget(loc);
				player.playAnimation(Animation.create(-1));
				stop();
			}
		});
	}

}
