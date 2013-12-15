package org.hyperion.rs2.content.combat;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;

/**
 * Combat Checks that need to be initialized.
 * @author phil The creator duh!
 *
 */
public class CombatCheck {

	/**
	 * Checks the position of the Entity.
	 * @param e The checking entity.
	 * @return If valid to continue.
	 */
	public static boolean correctAttributes(Entity e, Entity ot) {
		if(ot.isDead() || ot.getHealth() <= 0 || ot.isTeleporting() || ot instanceof NPC && ((NPC)ot).isInvisible()//Session check should be changed.
				|| ot instanceof Player && !((Player)ot).getSession().isConnected()) {//spell will continue if the attack is dead.
			CombatCheck.endCombat(e);
			return false;
		}
		//Check your state.
		if(e.getLastAttacked() != null && e.getLastAttacked() != ot && !isInMultiZone(e.getLocation())
				|| e.getLastAttacked() != null && e.getLastAttacked() != ot && !isInMultiZone(ot.getLocation())) {
			if(e instanceof Player)
				((Player)e).getActionSender().sendMessage("You are already under attack.");
			e.setCurrentTarget(null);
			return false;
		}
		//Check other entity state.
		if(ot.getLastAttacked() != null && ot.getLastAttacked() != e && !isInMultiZone(e.getLocation())
				|| ot.getLastAttacked() != null && ot.getLastAttacked() != e && !isInMultiZone(ot.getLocation())) {
			if(e instanceof Player)
				((Player)e).getActionSender().sendMessage("This "+(ot instanceof Player ? "player" : "monster") + " is already in combat.");
			return false;
		}
		if(e instanceof Player && ot instanceof Player) {
			if(isInWilderness(e.getLocation()) && !isInWilderness(ot.getLocation())//Wilderness Check.
					|| !isInWilderness(e.getLocation()) && isInWilderness(ot.getLocation())) {
				((Player)e).getActionSender().sendMessage("You can only attack other players in the wilderness.");
				return false;
			}
			if(isInWilderness(e.getLocation()) && isInWilderness(ot.getLocation())) {
				int difference = Math.abs(((Player)e).getSkills().getCombatLevel() -  ((Player)ot).getSkills().getCombatLevel());
				int lv1 = Math.abs(wildernessLevel(e.getLocation()));
				int lv2 = Math.abs(wildernessLevel(ot.getLocation()));
				if(lv1 < difference || lv2 < difference) {
					((Player)e).getActionSender().sendMessage("You need to move deeper into the wilderness.");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Declare the Entity out of combat. (This will fix most combat bugs.)
	 * @param a The entity to be ended.
	 */
	public static void endCombat(Entity a) {
		a.setCurrentTarget(null);
		a.setLastAttacked(null);
		
		//Check if other needs refreshing.
		if(a.getCurrentTarget() != null && !isInMultiZone(a.getCurrentTarget().getLocation())) {
			a.getCurrentTarget().setCurrentTarget(null);
			a.getCurrentTarget().setLastAttacked(null);
		}
	}
	
	/**
	 * Gets the wilderness level of the current position.
	 * @param position The level of position.
	 * @return Widlerness level.
	 */
	public static int wildernessLevel(Location position) {
		return ((position.getY() - 3520) / 8)+1;
	}
	
	/**
	 * Determines if the location is in the wilderness.
	 * @param loc The location.
	 * @return In Wilderness.
	 */
	public static boolean isInWilderness(Location loc) {
		return 
		loc.getX() >= 2949 && loc.getX() <= 3400 && loc.getY() >= 3525 && loc.getY() <= 4462 && loc.getZ() == 0 
			|| Constants.PVP_WORLD;
	}
	
	/**
	 * Determines if the location is in a MultiCombat zone.
	 * @param loc The location.
	 * @return In wilderness.
	 */
	public static boolean isInMultiZone(Location loc) {
		return loc.getX() >= 3136 && loc.getX() <= 3327 && loc.getY() >= 3520 && loc.getY() <= 3607
	        	|| loc.getX() >= 3190 && loc.getX() <= 3327 && loc.getY() >= 3648 && loc.getY() <= 3839 
	        	|| loc.getX() >= 3200 && loc.getX() <= 3390 && loc.getY() >= 3840 && loc.getY() <= 3967
	        	|| loc.getX() >= 2992 && loc.getX() <= 3007 && loc.getY() >= 3912 && loc.getY() <= 3967
	        	|| loc.getX() >= 2946 && loc.getX() <= 2959 && loc.getY() >= 3816 && loc.getY() <= 3831
	            || loc.getX() >= 3008 && loc.getX() <= 3199 && loc.getY() >= 3856 && loc.getY() <= 3903
	            || loc.getX() >= 3008 && loc.getX() <= 3071 && loc.getY() >= 3600 && loc.getY() <= 3711
	            || loc.getX() >= 3072 && loc.getX() <= 3327 && loc.getY() >= 3608 && loc.getY() <=3647
	            || loc.getX() >= 2582 && loc.getY() >= 5702 && loc.getX() <= 2620 && loc.getY() <= 5749
	            || loc.getX() >= 2240 && loc.getY() >= 4668 && loc.getX() <= 2300 && loc.getY() <= 4730
	            || loc.getX() > 3022 && loc.getX() < 3390 && loc.getY() > 3545 && loc.getY() < 3680
				|| loc.getX() > 2946 && loc.getX() < 3004 && loc.getY() > 3333 && loc.getY() < 3424
				|| loc.getX() > 3184 && loc.getX() < 3202 && loc.getY() > 3858 && loc.getY() < 3791
				|| loc.getX() > 3193 && loc.getX() < 3332 && loc.getY() > 3667 && loc.getY() < 3752
				|| loc.getX() > 3203 && loc.getX() < 3331 && loc.getY() > 3519 && loc.getY() < 3666
				|| loc.getX() > 3134 && loc.getX() < 3328 && loc.getY() > 3519 && loc.getY() < 3667
				|| loc.getX() > 2945 && loc.getX() < 2961 && loc.getY() > 3812 && loc.getY() < 3828;
	}
	
	/**
	 * Random
	 * @param o The random Integer, long, or Double.
	 * @return The result.
	 */
	public static int random(Object o) {
		return (int) (java.lang.Math.random() * ((Integer) o + 1));
	}
}
