package org.hyperion.rs2.content;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hyperion.util.PersistenceManager;

/**
 * Start of a NPC Bonus system. Easier to use, and and manage.
 * @author phil
 * - Load VIA XML until private development starts.
 */
public class NPCBonus {
	
	/**
	 * Holds the NPCAttack Bonuses.
	 */
	public static final Map<Short, NPCBonus> bonuses = new HashMap<Short, NPCBonus>();
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(NPCBonus.class.getName());

	private short npcId;
		
	private Bonuses[] bonus;
	
	public static class Bonuses {
		
		private short meleeAtt;
		private short rangedAtt;
		private short magicAtt;
		
		private short meleeDef;
		private short rangedDef;
		private short magicDef;

		/**
		 * @return the melee attack bonus
		 */
		public short getMeleeAttackBonus() {
			return meleeAtt;
		}
		
		/**
		 * @return the ranged attack bonus
		 */
		public short getRangedAttackBonus() {
			return rangedAtt;
		}
		
		/**
		 * @return the magic attack bonus
		 */
		public short getMagicAttackBonus() {
			return magicAtt;
		}
		
		/**
		 * @return the magic defence bonus
		 */
		public short getMeleeDefenceBonus() {
			return meleeDef;
		}
		
		/**
		 * @return the ranged defence bonus
		 */
		public short getRangedDefenceBonus() {
			return rangedDef;
		}
		
		/**
		 * @return the magic defence bonus
		 */
		public short getMagicDefenceBonus() {
			return magicDef;
		}
	}
	
	/**
	 * @return the npcId
	 */
	public short getNpcId() {
		return npcId;
	}
	
	/**
	 * @return the bonuses
	 */
	public Bonuses[] getBonuses() {
		return bonus;
	}
	
	@SuppressWarnings("unchecked")
	public static void init() {
		try {
			List<NPCBonus> loadedData = (List<NPCBonus>) PersistenceManager.load(new FileInputStream("./data/npcbonuses.xml"));
			for (NPCBonus data : loadedData)
			{
				bonuses.put(data.npcId, data);
			}
			logger.info("Successfully loaded "+loadedData.size() +" npc bonuses.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
