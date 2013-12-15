package org.hyperion.rs2.action.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

/**
 * An action for cutting down trees.
 * @author Graham Edgecombe
 * @author Stephen
 *
 *TODO - 
 * Cut tree faster based on axe and level
 * Random events
 * Tick for trees
 */
public class WoodcuttingAction extends HarvestingAction {
	
	/**
	 * Represents types of axes.
	 * @author Graham Edgecombe
	 *
	 */
	public static enum Axe {
		
		/**
		 * Rune axe.
		 */
		RUNE(1359, 41, 867),
		
		/**
		 * Adamant axe.
		 */
		ADAMANT(1357, 31, 869),
		
		/**
		 * Mithril axe.
		 */
		MITHRIL(1355, 21, 871),
		
		/**
		 * Black axe.
		 */
		BLACK(1361, 6, 873),
		
		/**
		 * Steel axe.
		 */
		STEEL(1353, 6, 875),
		
		/**
		 * Iron axe.
		 */
		IRON(1349, 1, 877),
		
		/**
		 * Bronze axe.
		 */
		BRONZE(1351, 1, 879);
		
		/**
		 * The id.
		 */
		private short id;
		
		/**
		 * The level.
		 */
		private byte level;
		
		/**
		 * The animation.
		 */
		private short animation;
		
		/**
		 * A map of object ids to axes.
		 */
		private static Map<Short, Axe> axes = new HashMap<Short, Axe>();
		
		/**
		 * Gets a axe by an object id.
		 * @param object The object id.
		 * @return The axe, or <code>null</code> if the object is not a axe.
		 */
		public static Axe forId(int object) {
			return axes.get(object);
		}
		
		/**
		 * Populates the tree map.
		 */
		static {
			for(Axe axe : Axe.values()) {
				axes.put(axe.id, axe);
			}
		}
		
		/**
		 * Creates the axe.
		 * @param id The id.
		 * @param level The required level.
		 * @param animation The animation id.
		 */
		private Axe(int id, int level, int animation) {
			this.id = (short) id;
			this.level = (byte) level;
			this.animation = (short) animation;
		}
		
		/**
		 * Gets the id.
		 * @return The id.
		 */
		public int getId() {
			return id;
		}
		
		/**
		 * Gets the required level.
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}
		
		/**
		 * Gets the animation id.
		 * @return The animation id.
		 */
		public int getAnimation() {
			return animation;
		}
		
	}
	
	/**
	 * Represents types of trees.
	 * @author Graham Edgecombe
	 *
	 */
	public static enum Tree {
		
		/**
		 * Normal tree.
		 */
		NORMAL(1511, 1342, 15, 1, 50, new short[] { 1276, 1277, 1278, 1279, 1280,
			1282, 1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315, 1316, 1318,
			1319, 1330, 1331, 1332, 1365, 1383, 1384, 2409, 3033, 3034, 3035,
			3036, 3881, 3882, 3883, 5902, 5903, 5904 }),
		
		/**
		 * Willow tree.
		 */
		WILLOW(1519, 7399, 15, 30, 135, new short[] { 1308, 5551, 5552, 5553 }),
		
		/**
		 * Oak tree.
		 */
		OAK(1521, 1356, 2, 5, 75, new short[] { 1281, 3037 }),
		
		/**
		 * Magic tree.
		 */
		MAGIC(1513, 1342, 15, 75, 500, new short[] { 1292, 1306 }),
		
		/**
		 * Maple tree.
		 */
		MAPLE(1517, 1342, 100, 45, 200, new short[] { 1307, 4677 }),
		
		/**
		 * Mahogany tree.
		 */
		MAHOGANY(6332, 1342, 15, 50, 250, new short[] { 9034 }),
		
		/**
		 * Teak tree.
		 */
		TEAK(6333, 1342, 15, 35, 170, new short[] { 9036 }),
		
		/**
		 * Achey tree.
		 */
		ACHEY(2862, 1342, 15, 1, 50, new short[] { 2023 }),
		
		/**
		 * Yew tree.
		 */
		YEW(1515, 1342, 15, 60, 350, new short[] { 1309 });
		
		/**
		 * A map of object ids to trees.
		 */
		private static Map<Short, Tree> trees = new HashMap<Short, Tree>();
		
		/**
		 * Gets a tree by an object id.
		 * @param object The object id.
		 * @return The tree, or <code>null</code> if the object is not a tree.
		 */
		public static Tree forId(int object) {
			return trees.get((short)object);
		}
		
		/**
		 * Populates the tree map.
		 */
		static {
			for(Tree tree : Tree.values()) {
				for(short object : tree.objects) {
					trees.put(object, tree);
				}
			}
		}
		
		/**
		 * The object ids of this tree.
		 */
		private short[] objects;
		
		/**
		 * The minimum level to cut this tree down.
		 */
		private byte level;
		
		/**
		 * The id of the tree.
		 */
		private int treeId;
		
		/**
		 * The log of this tree.
		 */
		private short log;
		
		/**
		 * The stump of this tree.
		 */
		private short stump;
		
		/**
		 * The amount of time the stump appears after the tree is cut down.
		 */
		private short stumpTime;
		
		/**
		 * The experience.
		 */
		private double experience;
		
		/**
		 * Creates the tree.
		 * @param log The log id.
		 * @param level The required level.
		 * @param experience The experience per log.
		 * @param objects The object ids.
		 */
		private Tree(int log, int stump, int stumpTime, int level, double experience, short[] objects) {
			this.objects = objects;
			this.level = (byte) level;
			this.experience = experience;
			this.log = (short) log;
			this.stump = (short) stump;
			this.stumpTime = (short) stumpTime;
		}
		
		/**
		 * Gets the log id.
		 * @return The log id.
		 */
		public int getLogId() {
			return log;
		}
		
		/**
		 * Gets the stump id.
		 * @return The stump id.
		 */
		public int getReplacementObject() {
			return stump;
		}
		
		/**
		 * Gets the time a stump will appear.
		 * @return The stump time.
		 */
		public int getStumpTime() {
			return stumpTime;
		}
		
		/**
		 * Gets the object ids.
		 * @return The object ids.
		 */
		public short[] getObjectIds() {
			return objects;
		}
		
		/**
		 * Gets the required level.
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}
		
		/**
		 * Gets the experience.
		 * @return The experience.
		 */
		public double getExperience() {
			return experience;
		}
		
		/**
		 * Gets the tree id.
		 * @return The tree id.
		 */
		public int getTreeId() {
			return treeId;
		}
		
		/**
		 * Sets the id of the tree being cut.
		 */
		public void setTreeId(int id) {
			treeId = id;
		}
		
	}
	
	/**
	 * The delay.
	 */
	private static final int DELAY = 3000;
	
	/**
	 * The factor.
	 */
	private static final double FACTOR = 0.5;
	
	/**
	 * Whether or not this action requires an object replacement.
	 */
	private static final boolean REPLACEMENT = true;
	
	
	/**
	 * Whether or not this action grants periodic rewards.
	 */
	private static final boolean PERIODIC = true;
	
	/**
	 * The axe type.
	 */
	private Axe axe;
	
	/**
	 * The tree type.
	 */
	private Tree tree;

	/**
	 * Creates the <code>WoodcuttingAction</code>.
	 * @param player The player performing the action.
	 * @param tree The tree.
	 */
	public WoodcuttingAction(Player player, Location location, Tree tree) {
		super(player, location);
		this.tree = tree;
	}
	
	@Override
	public long getHarvestDelay() {
		return DELAY;
	}
	
	@Override
	public boolean requiresReplacementObject() {
		return REPLACEMENT;
	}
	
	@Override
	public boolean getPeriodicRewards() {
		return PERIODIC;
	}
		
	@Override
	public void init() {
		final Player player = getPlayer();
		final int wc = player.getSkills().getLevel(Skills.WOODCUTTING);
		for(Axe axe : Axe.values()) {
			if((player.getEquipment().contains(axe.getId()) || player.getInventory().contains(axe.getId())) && wc >= axe.getRequiredLevel()) {
				this.axe = axe;
				break;
			}
		}
		if(axe == null) {
			player.getActionSender().sendMessage("You do not have an axe that you can use.");
			stop();
			return;
		}
		if(wc < tree.getRequiredLevel()) {
			player.getActionSender().sendMessage("You do not have the required level to cut down that tree.");
			stop();
			return;
		}
		player.getActionSender().sendMessage("You swing your axe at the tree...");
	}

	@Override
	public int getCycles() {
		if(tree == Tree.NORMAL) {
			return 1;
		} else {
			return new Random().nextInt(5) + 5; //Not working properly - got 2 logs
		}
	}

	@Override
	public double getFactor() {
		return FACTOR;
	}
	
	@Override
	public int getReplacementObject() {
		return tree.getReplacementObject();
	}
	
	@Override
	public Item getHarvestedItem() {
		return new Item(tree.getLogId(), 1);
	}

	@Override
	public double getExperience() {
		return tree.getExperience();
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(axe.getAnimation());
	}

	@Override
	public int getSkill() {
		return Skills.WOODCUTTING;
	}
	
	@Override
	public int getObjectId() {
		return tree.getTreeId();
	}
	
	@Override
	public int getRespawnTime() {
		return tree.getStumpTime();
	}
}
