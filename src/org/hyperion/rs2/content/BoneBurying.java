package org.hyperion.rs2.content;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

/**
 * This class implements code related to all burying bones. 
 * 
 * All of the data is an accurate representation of Runescape itself.
 * Data has been collected from <url>http://runescape.wikia.com/wiki/Calculator:Prayer/Bones</url>
 * @author Stephen
 */

public class BoneBurying {

	enum Bone {
			
			/**
			 * Regular bones.
			 */
			REGULAR(526, 4.5),
			
			/**
			 * Regular bones.
			 */
			WOLF(2859, 4.5),
			
			/**
			 * Regular bones.
			 */
			BURNT(528, 4.5),
			
			/**
			 * Regular bones.
			 */
			MONKEY(3179, 5),
			
			/**
			 * Regular bones.
			 */
			BAT(530, 5.3),
			
			/**
			 * Big bones.
			 */
			BIG(532, 15),
			
			/**
			 * Jogre bones.
			 */
			JOGRE(3125, 15),
			
			/**
			 * Zogre bones.
			 */
			Zogre(4812, 22.5),
			
			/**
			 * Shaikahan bones.
			 */
			SHAIKAHAN(3123, 25),
			
			/**
			 * Baby dragon bones.
			 */
			BABY_DRAGON(534, 30),
			
			/**
			 * Wyvern bones.
			 */
			WYVERN(6812, 50),
			
			/**
			 * Dragon bones.
			 */
			DRAGON(536, 72),
			
			/**
			 * Fayrg bones.
			 */
			FAYRG(4830, 84),
			
			/**
			 * Raurg bones.
			 */
			RAURG(4832, 96),
			
			/**
			 * Dagannoth bones.
			 */
			DAGANNOTH(6729, 125),
			
			/**
			 * Airut bones.
			 */
			AIRUT(526, 132.5),
			
			/**
			 * Ourg bones.
			 */
			OURG(4834, 140),
			
			/**
			 * Frost dragon bones (#614).
			 */
			FROST_DRAGON(18830, 180);
			
			
			/**
			 * The id.
			 */
			short id;
			
			/**
			 * The experience.
			 */
			short experience;
			
			
			Bone(int id, double experience) {
				this.id = (short) id;
				this.experience = (short) experience;
			}
			
			/**
			 * Map of bone info.
			 */
			private static Map<Short, Bone> bones = new HashMap<Short, Bone>();
			
			static {
				for(Bone b : Bone.values())
					bones.put(b.id, b);
			}
			
			/**
			 * Gets bone data.
			 * @param id The boneId.
			 * @return The boneData.
			 */
			public static Bone getInfo(int id) {
				return bones.get((short)id);
			}
			
			/**
			 * Gets the animation id.
			 * @return The animation id.
			 */
			public double getExperience() {
				return experience;
			}	
		}
	
	/**
	 * Buries the bone from the clicked slot.
	 * @param player The player burying the bones.
	 * @param itemSlot The itemSlot the bones are in.
	 */
	public static void buryBone(Player player, int itemSlot) {
		Item clicked = player.getInventory().get(itemSlot);
		if(clicked == null || player.getBoneTimer() > 0) return;
		Bone bone = Bone.getInfo(clicked.getId());
		if(bone == null) return;		if(clicked.getCount() > 1) {
			player.getInventory().remove(new Item(clicked.getId(), 1));
		} else {
			player.getInventory().set(itemSlot, null);
		}
		player.playAnimation(Animation.create(827));
		player.setBoneTimer((byte)2);
		if(!player.isAutoRetaliating())
			player.setCurrentTarget(null);
		if(player.getLastAttack() < 3)
			player.setLastAttack(3);
		if(player.getAbstractMagicDelay() < 3)
			player.setAbstractMagicDelay(3);
		player.getSkills().addExperience(Skills.PRAYER, bone.getExperience() * Constants.SKILL_EXPERIENCE);
		player.getActionSender().sendMessage("You bury the " + clicked.getDefinition().getName() + ".");
	}
}
