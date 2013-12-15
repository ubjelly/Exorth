package org.hyperion.rs2.content;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

/**
 * Handles bone burying.
 * @author Stephen
 */

// created this class to eat a shitload of bananas???

public class BoneBurying {

	enum Bone {
			
			/**
			 * Regular bones.
			 */
			REGULAR(526, 126),
			
			/**
			 * Big bones.
			 */
			BIG(532, 254);
			
			/**
			 * The id.
			 */
			short id;
			
			/**
			 * The experience.
			 */
			short experience;
			
			
			Bone(int id, int experience) {
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
	 * Eats the food from the slot.
	 * @param player The player.
	 * @param itemSlot ItemSlot.
	 */
	public static void buryBone(Player player, int itemSlot) {
		Item clicked = player.getInventory().get(itemSlot);
		if(clicked == null) return;
		Bone bone = Bone.getInfo(clicked.getId());
		if(bone == null) return;
		player.getSkills().addExperience(Skills.PRAYER, bone.getExperience());
		if(clicked.getCount() > 1) {
			player.getInventory().remove(new Item(clicked.getId(), 1));
		} else {
			player.getInventory().set(itemSlot, null);
		}
		player.playAnimation(Animation.create(827));
		//player.setFoodTimer((byte)3);
		if(!player.isAutoRetaliating())
			player.setCurrentTarget(null);
		if(player.getLastAttack() < 3)
			player.setLastAttack(3);
		if(player.getAbstractMagicDelay() < 3)
			player.setAbstractMagicDelay(3);
		player.getActionSender().sendMessage("You bury the " + clicked.getDefinition().getName() + ".");
	}
}
