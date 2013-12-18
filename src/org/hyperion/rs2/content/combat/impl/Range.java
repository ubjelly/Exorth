package org.hyperion.rs2.content.combat.impl;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.content.Bonus;
import org.hyperion.rs2.content.DegradeSystem;
import org.hyperion.rs2.content.NPCBonus;
import org.hyperion.rs2.content.NPCStyle;
import org.hyperion.rs2.content.Poison;
import org.hyperion.rs2.content.Projectile;
import org.hyperion.rs2.content.ProjectileManager;
import org.hyperion.rs2.content.combat.Combat;
import org.hyperion.rs2.content.combat.CombatCheck;
import org.hyperion.rs2.content.combat.CombatEffect;
import org.hyperion.rs2.content.combat.WeaponStyle;
import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.event.impl.FloorItemEvent;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.Definitions;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.FloorItem;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.tick.Tick;

public class Range {

	/**
	 * Executes Range Combat.
	 * @param atker The atker.
	 * @param victim The victim.
	 */
	public static void execute(final Entity atker, final Entity victim) {
		//Check to see if we should continue.
		if(!CombatCheck.correctAttributes(atker, victim)) {
			//Declare out of combat.
			CombatCheck.endCombat(atker);
			return;
		}
		
		//Distance checking - Basic - Should check lineOfSite also.
		if(!atker.getLocation().withinRange(victim.getLocation(), atker instanceof Player ? WeaponStyle.getStyle(((Player)atker)) == WeaponStyle.STYLE_LONG_RANGE ? 8 : 5 : 8)) {//wird its 8 spaces away
			//Follow to open spot
			return;
		} else {
			atker.getWalkingQueue().reset();//Dont walk if within range.
		}
		
		//Combat Delay.
		if(atker.getLastAttack() > 0) return;
		
		//face Opponent
		if(atker.getInteractingEntity() == null)
			atker.setInteractingEntity(victim);
		
		//Set the target.
		atker.setCurrentTarget(victim);
		
		//Ammo (Can be disregarded in some cases).
		final int ammo = atker instanceof NPC || ((Player)atker).getEquipment().isSlotFree(Equipment.SLOT_ARROWS) ? 882 : ((Player)atker).getEquipment().get(Equipment.SLOT_ARROWS).getId();
		
		//bow being used(Can Also be ammo).
		final int bow = atker instanceof NPC || ((Player)atker).getEquipment().isSlotFree(Equipment.SLOT_WEAPON) ? 841 : ((Player)atker).getEquipment().get(Equipment.SLOT_WEAPON).getId();
		//Is using arrows or hand projectiles.
		final boolean useAmmo = isUsingAmmo(bow);
		
		//If using a knife or bow.
		final boolean handProjectile = Definitions.forId(bow).isStackable() && useAmmo;
		
		//special enabled.
		final boolean specOn = false;
		
		//Checks.
		if(atker instanceof Player) {
			Player p = (Player) atker;
			if(useAmmo && ammo == -1 && !handProjectile) {
				((Player)atker).getActionSender().sendMessage("You do not have any arrows in your quiver.");
				atker.setCurrentTarget(null);//Do not declare out of combat, just return the combat.
				return;
			}
			//Check variable set apon equip.
			if(!p.hasUsableAmmo()) {
				p.getActionSender().sendMessage("You cannot use these arrows with this bow.");
				atker.setCurrentTarget(null);
				return;
			}
		}
		
		//Remove items
		if(atker instanceof Player)
			((Player)atker).getEquipment().remove(new Item(handProjectile ? bow : ammo));
		
		//Calculate maxHit.
		int max = getMaxHit(atker, victim, specOn);
		
		//NPC Attack info
		final NPCStyle s = atker instanceof NPC ? NPCStyle.attacks.get(((NPC)atker).getId()) : null;
		
		if(s == null && atker instanceof NPC) return;
		
		//TODO other effects.
		
		//Animation
		atker.playAnimation(Animation.create(atker instanceof NPC ? s.getAtts()[atker.getFightIndex()].getAnim() : ((Player)atker).getAttackEmote()));
	
		//Pullback GFX
		atker.playGraphics(Graphic.create(drawBackGFX(bow, ammo), 95 << 16));
		
		//Projectile gfx
		short proj = (short) (atker instanceof NPC ? s.getAtts()[atker.getFightIndex()].getGfx().length >= 2 ? s.getAtts()[atker.getFightIndex()].getGfx()[2] : -1 : projectile(bow, ammo));
		
		//Graphics - if there is any,
		Projectile p = ProjectileManager.getProjectile(proj);
		
		//Send projectile.
		//TODO - add d bow and specials.
		if(bow != 11235)
			ProjectileManager.createProjectile(atker, victim, p);
		
		//Delays *Thanks to Maxi*
		double splatDelay = /*p.getDelay() + p.getSlowness() + (atker.getLocation().getDistanceFromLocation(victim.getLocation())-1) * 5*/ 7;
		final int delay = (int) Math.ceil((splatDelay * 12d) / 600d);
		
		//defend emote.
		if(victim.getCurrentAnimation() == null)
			victim.playAnimation(Animation.create(victim instanceof Player ? ((Player)victim).getDefEmote() : NPCStyle.attacks.get(((NPC)victim).getId()).getDefAnim(), (int) splatDelay-20));
		
		//Hits to be done.
		int hitCount = 1;
		
		//World
		for(int i = 0; i < hitCount; i++) {
			//Damage.
			final int hit = success(atker, victim) ? altarDamage(atker, victim, CombatCheck.random(max)) : 0;
			
			//final variable I
			final int hitNum = i;
			
			World.getWorld().submit(new Tick(hitNum == 0 ? 1 : delay) {//Add a delay to multihits.
	
				//dmg for this attack.
				int thisHit = hit;
				
				//tickStage
				int tick = 0;
				
				@Override
				protected void execute() {
					// TODO Auto-generated method stub
					
					//Check if dead.
					if(victim.isDead() || victim.getHealth() <= 0 || victim.isTeleporting()) {
						this.stop();
						CombatCheck.endCombat(atker);
						return;
					}
					
					//Block anim
					if(tick++ == 0 && hitNum == 0) {
						setDelay(delay - 1);
						if(victim.getPoisDmg() <= 0 && useAmmo && Definitions.forId(!handProjectile ? ammo : bow).getPoisonDmg() != 255) {
							int pois = Definitions.forId(!handProjectile ? ammo : bow).getPoisonDmg();
							Poison.poison(victim, pois);
						}
						//Degrade, tick after animation.
						if(atker instanceof Player)
							DegradeSystem.checkDegrade(((Player)atker), true);
						if(atker instanceof NPC)
							((NPC)atker).distributeAttackEffects(victim, thisHit);
						//Block animation
						if(victim.getCurrentAnimation() == null)
							victim.playAnimation(Animation.create(victim instanceof NPC ? NPCStyle.attacks.get(((NPC)victim).getId()).getDefAnim() : ((Player)victim).getDefEmote()));
						return;
					}
					if(atker instanceof Player && useAmmo && CombatCheck.random(2) == 0)
						FloorItemEvent.addFloorItem(new FloorItem(handProjectile ? bow : ammo, 1, victim.getLocation(), atker, atker, false));
					
					//Hit.
					victim.inflictDamage(new Hit(thisHit > victim.getHealth() ? victim.getHealth() : thisHit, thisHit <= 0 ? HitType.NO_DAMAGE : HitType.NORMAL_DAMAGE), atker);
					if(thisHit > 0)
						victim.addDmgRecieved(atker, thisHit);
					
					
					/* Handle Agressive NPCs */
					if(atker instanceof NPC && CombatCheck.isInMultiZone(atker.getLocation())
							&& atker.getAggressorState())
						atker.setCurrentTarget(null);//Change opponents.
					
					this.stop();
				}
				
			});
			//add XP
			if(atker instanceof Player) {
				CombatEffect.giveExperience(((Player)atker), hit, Combat.RANGE);
				((Player)atker).setAbstractMagicDelay(2);
			}
		}
		//Speed to be assigned.
		int speed= atker instanceof NPC ? s.getAtts()[atker.getFightIndex()].getSpeed() : Definitions.forId(bow).getSpeed();
		if(atker instanceof Player && WeaponStyle.getStyle(((Player)atker)) == WeaponStyle.STYLE_RAPID)
			speed--;
		atker.setLastAttack(speed <= 0 ? 4 : speed);
	}
	
	/**
	 * Determines how successful the next hit will be.
	 * @param e
	 * @param o
	 * @return
	 */
	public static boolean success(Entity e, Entity o) {
		int atk = 1, def = 10;
		if(e instanceof Player) {
			Player p = (Player) e;
			int cLv = p.getSkills().getLevel(Skills.RANGE);
			if(p.getFightStyle() == WeaponStyle.STYLE_ACCURATE)
				cLv += 3;
			int bonus = p.getBonus()[Bonus.RANGE_ATTACK];
			double cBoost = 1.00;
			//prayers
			cLv = (int) Math.floor(cLv * cBoost) + 8 + bonus;
			//blah
			//end
			atk = cLv * (1 + bonus / 64);
		} else {
			
		}
		
		if (e instanceof NPC){
			NPC p = (NPC)e;
			atk = (int) NPCBonus.bonuses.get(((NPC)p).getId()).getBonuses()[p.getFightIndex()].getRangedAttackBonus();
			System.out.println("ranged attackbonus: " + atk);
		}
		
		if(o instanceof Player) {
			Player p = (Player) o;
			int cLv = p.getSkills().getLevel(Skills.DEFENCE)/2 + p.getSkills().getLevel(Skills.AGILITY)/2;
			if(p.getFightStyle() == WeaponStyle.STYLE_LONG_RANGE || p.getFightStyle() == WeaponStyle.STYLE_DEFENSIVE)
				cLv += 3;
			int bonus = p.getBonus()[Bonus.RANGE_DEF];
			double cBoost = 1.00;
			if(p.getPrayers()[Prayer.ROCK_SKIN])
				cBoost *= 1.10;
			else if(p.getPrayers()[Prayer.THICK_SKIN])
				cBoost *= 1.05;
			else if(p.getPrayers()[Prayer.STEEL_SKIN])
				cBoost *= 1.15;
			cLv = (int) Math.floor(cLv * cBoost) + 8 + bonus;
			def = cLv * (1 + bonus / 64);
		} else {
			
		}
		int accuracy = atk > def ? 1 - (def+1) / (2 * atk) : (atk-1) / (2* def); 
		return (accuracy * 100) >= CombatCheck.random(99)+1;
	}
	
	/**
	 * Gets the drawbackGFX of the bow/weapon.
	 * @param bow The bow.
	 * @param ammo The ammo.
	 * @return The blah.
	 */
	public static int drawBackGFX(int bow, int ammo) {
		if(bow >= 4212 && bow <= 4223)
			return 472;
			//knive;
		if(bow == 864)
			return 219;
		if(bow != 11235)//dark bow check.
			if(ammo == 882)
				return 19;
			else if(ammo == 884)
				return 18;
			else if(ammo == 886)
				return 20;
			else if(ammo == 888)
				return 21;
			else if(ammo == 890)
				return 22;
			else if(ammo == 892)
				return 23;
			else
				return -1;
		return -1;
	}
	
	/**
	 * If using ammo.
	 * @param bow The bow,
	 * @return Uses the ammo.
	 */
	public static boolean isUsingAmmo(int bow) {
		return !(bow >= 4212 && bow <= 4223);//Only supported ones atm.
	}
	
	/**
	 * Gets the Projectile to be sent.
	 * @param bow The bow to be used.
	 * @param ammo The ammo type being used.
	 * @return Blah.
	 */
	public static int projectile(int bow, int ammo) {
		if(bow >= 4212 && bow <= 4223)
			return 471;
		//bow
		if(bow == 864)
			return 212;
		//arrows
		if(ammo == 882)
			return 10;
		else if(ammo == 884)
			return 9;
		else if(ammo == 886)
			return 11;
		else if(ammo == 888)
			return 12;
		else if(ammo == 890)
			return 13;
		else if(ammo == 892)
			return 15;
		return 0;
	}
	
	/**
	 * Determines if hit has been altar'd
	 * @param e The caster.
	 * @param o The opponent.
	 * @param hit The initial Damage.
	 * @return The final hit.
	 */
	public static int altarDamage(Entity e, Entity o, int hit) {
		if(e instanceof NPC && o instanceof Player && ((Player)o).getPrayers()[Prayer.PROTECT_FROM_MAGE])
			return (int) (hit - (hit * NPCStyle.attacks.get(((NPC)e).getId()).getAtts()[e.getFightIndex()].getBlockPercentile()));
		if(e instanceof NPC) return hit;
		if(e instanceof Player && o instanceof Player && ((Player)o).getPrayers()[Prayer.PROTECT_FROM_MISSILES])
			return (int) (hit * 0.40);
		return hit;
	}
	
	/**
	 * Gets the MaxHit that the entity is able to hit.
	 * @param e The atker.
	 * @param o The victim.
	 * @return The max hit based on the current condition.
	 */
	public static int getMaxHit(Entity e, Entity o, boolean spec) {
		if(e instanceof NPC) return (int) NPCStyle.attacks.get(((NPC)e).getId()).getAtts()[e.getFightIndex()].getMax();
		Player p = (Player) e;
		int a = p.getSkills().getLevel(Skills.RANGE);
		double b = 1.00;
		int e2 = p.getFightStyle() == WeaponStyle.STYLE_ACCURATE ? 3 : 0;
		int c = (int) Math.floor(a * b + e2);
		int d = p.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON) ? p.getEquipment().get(Equipment.SLOT_WEAPON).getDefinition().getBonus()[Bonus.RANGED_STR_BONUS] : 0;
		if(d <= 0)
			d = p.getBonus()[Bonus.RANGED_STR_BONUS];
		int max = (5 + c * (1 + d/64));
		return (int) (Constants.CONSTITUTION_ENABLE ? max : Math.ceil(max/10) <= 0 ? 1 : Math.ceil(max/10));
	}
}
