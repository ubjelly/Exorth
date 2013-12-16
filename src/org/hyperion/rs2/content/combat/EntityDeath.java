package org.hyperion.rs2.content.combat;

import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.event.impl.FloorItemEvent;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.FloorItem;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Container.Type;

/**
 * Entity Death.
 * @author phil
 *
 */
public class EntityDeath {
	/**
	 * Drops items for the player based on their items + Prayers.
	 * @param p The player dead.
	 */
	public static void playerDied(Player p) {
		Container holder = new Container(Type.STANDARD, p.getEquipment().size() + p.getInventory().size());
		//add bones.
				
		int save = p.getSkullIcon() != -1 ? 0 : 3;//No Skull | Prayer Support.
		
		if(p.getPrayers()[Prayer.PROTECT_ITEM])
			save++;
		
		for(Item i : p.getEquipment().toArray())
			if(i != null)
				holder.add(i);
		p.getEquipment().clear();
		
		for(Item i : p.getInventory().toArray())
			if(i != null){
				holder.add(i);
			}
		p.getInventory().clear();
		
		///Sort Items by alch value.
		//end
		
		//get killer
		Entity killer = p.inflictedMostDamage();
		
		for(Item i : holder.toArray()) {
			if(i != null){
				FloorItemEvent.addFloorItem(new FloorItem(i.getId(), i.getCount(), p.getLocation(), p, killer instanceof Player && killer != null ? killer : p, false));
			}
		holder.clear();//Clear.
		}
		FloorItemEvent.addFloorItem(new FloorItem(526, 1, p.getLocation(), p, killer instanceof Player && killer != null ? killer : p, false));
	}
	
	/**
	 * Executes death effects.
	 * @param e The dead player.
	 */
	public static void executeDeathEffects(Player p) {
		//Retribution - too lazy to fnish.
		if(p.getPrayers()[Prayer.RETRIBUTION]) {
			@SuppressWarnings("unused")
			int baseDamage = (int) ((p.getSkills().getLevelForExperience(Skills.PRAYER) * 2.50) / 10);
			p.playGraphics(Graphic.create(437));
		}
	}
	
	/**
	 * Drops items based on the Dead Mob.
	 * @param n The dead Mob.
	 */
	public static void npcDied(NPC n) {
		Entity killer = n.inflictedMostDamage();
		FloorItemEvent.addFloorItem(new FloorItem(526, 1, n.getLocation(), killer, killer, false));
	}
}
